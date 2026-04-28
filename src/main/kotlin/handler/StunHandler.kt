package com.break2bits.handler

import com.break2bits.binary.string.toBinaryStr
import com.break2bits.binary.toByteArray
import com.break2bits.binary.xor
import com.break2bits.message.StunMessage
import com.break2bits.message.attribute.StunAttribute
import com.break2bits.message.attribute.StunAttributeType
import com.break2bits.message.attribute.StunAttributeValue
import com.break2bits.message.header.StunHeader
import com.break2bits.message.header.StunMessageType
import com.break2bits.serialize.attribute.StunAttributeWriter
import com.break2bits.serialize.attribute.XorMappedAddressValueSerializer
import com.break2bits.serialize.header.StunHeaderWriter
import com.sun.jdi.ByteValue
import java.net.InetAddress
import java.net.StandardProtocolFamily
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.zip.CRC32

class StunHandler(
    private val messageIntegrityValidator: MessageIntegrityValidator,
    private val xorMappedAddressValueSerializer: XorMappedAddressValueSerializer,
    private val stunHeaderWriter: StunHeaderWriter,
    private val stunAttributeWriter: StunAttributeWriter,
) {
    private companion object {
        private const val SOFTWARE_NAME = "stun-server-kt"
        private const val FINGERPRINT_VALUE_LENGTH_BYTES: UShort = 4u
    }

    fun handle(request: StunMessage, senderAddress: InetAddress, senderPort: Int): StunMessage? {
        if (request.header.messageType == StunMessageType.INDICATION_REQUEST) {
            return null
        }
        validateMessageType(request.header)

        // authentication is optional

        val hadFingerprint = maybeValidateFingerprintAttribute(request)
        val xorMappedAddressAttr = createXorMappedAddressAttribute(request.header, senderAddress, senderPort)
        val softwareAttr = createSoftwareAttribute()
        val attributes = mutableListOf(
            xorMappedAddressAttr,
            softwareAttr,
        )
        var fingerprintAttr: StunAttribute? = null
        if (hadFingerprint) { // if there was a fingerprint in the request we should add one to the response with a dummy value
            fingerprintAttr = StunAttribute(
                type = StunAttributeType.FINGERPRINT,
                valueLength = FINGERPRINT_VALUE_LENGTH_BYTES,
                value = ByteArray(FINGERPRINT_VALUE_LENGTH_BYTES.toInt()),
                offset = -1
            )
        }
        val header = buildResponseHeader(request, attributes, hadFingerprint)
        val output = ByteBuffer.allocate(header.messageLength.toInt())
        stunHeaderWriter.write(output, header)
        attributes.forEach {
            stunAttributeWriter.write(output, it)
        }
        if (fingerprintAttr != null) { // finally, update the value of the fingerprint attribute before we add it to the output
            setFingerprintValue(output, fingerprintAttr)
            attributes.add(fingerprintAttr)
            stunAttributeWriter.write(output, fingerprintAttr)
        }
        val message = StunMessage(
            header = header,
            attributes = attributes,
            bytes = output.array()
        )

        return message
    }

    private fun validateMessageType(message: StunHeader) {
        if (message.messageType != StunMessageType.BINDING_REQUEST) {
            val binStr = StunMessageType.BINDING_REQUEST.value.toBinaryStr()
            throw StunRequestException("Server only serves BINDING requests of message type $binStr")
        }
    }

    private fun maybeValidateFingerprintAttribute(message: StunMessage): Boolean {
        val maybeFingerprintAttribute = message.attributesByType[StunAttributeType.FINGERPRINT]
        if (maybeFingerprintAttribute == null) {
            return false
        }
        val offset = maybeFingerprintAttribute.attribute.offset
        val crc32 = CRC32()
        crc32.update(message.bytes, 0, offset)
        val crcValue = crc32.value
        val calculatedFingerprint = crcValue xor StunAttributeValue.FINGERPRINT_XOR
        val messageFingerprint = ByteBuffer.wrap(maybeFingerprintAttribute.attribute.value).getLong()

        if (calculatedFingerprint != messageFingerprint) {
            throw StunRequestException("${StunAttributeType.FINGERPRINT.name} attribute value ($${messageFingerprint.toHexString()}) not equal to calculated fingerprint: ${calculatedFingerprint.toHexString()}")
        }
        return true
    }

    private fun createSoftwareAttribute(): StunAttribute {
        var softwareAttrValueBytes = SOFTWARE_NAME.encodeToByteArray()
        val bytesToPrepend = softwareAttrValueBytes.size % 4
        if (bytesToPrepend > 0) { // the value must end on a 32-bit boundary
            val paddedSoftwareAttrValueBytes = ByteArray(softwareAttrValueBytes.size + bytesToPrepend)
            System.arraycopy(softwareAttrValueBytes, 0, paddedSoftwareAttrValueBytes, bytesToPrepend, softwareAttrValueBytes.size)
            softwareAttrValueBytes = paddedSoftwareAttrValueBytes
        }
        return StunAttribute(
            type = StunAttributeType.SOFTWARE,
            valueLength = softwareAttrValueBytes.size.toUShort(),
            value = softwareAttrValueBytes,
            offset = -1
        )
    }

    private fun createXorMappedAddressAttribute(header: StunHeader, senderAddress: InetAddress, senderPort: Int): StunAttribute {
        val magicCookieTopTwoBytes = getMagicCookieTopTwoBytesAsInt()
        val xPort = senderPort xor magicCookieTopTwoBytes

        val family: StandardProtocolFamily
        val xAddress: ByteArray
        if (senderAddress.address.size == XorMappedAddressValueSerializer.IPV4_ADDRESS_SIZE_BYTES) {
            // IPV4 xAddress calculation
            family = StandardProtocolFamily.INET
            xAddress = senderAddress.address.xor(StunHeader.MAGIC_COOKIE.toByteArray())
        } else if (senderAddress.address.size == XorMappedAddressValueSerializer.IPV6_ADDRESS_SIZE_BYTES) {
            // IPV6 xAddress calculation
            val magicCookieAndTxnId = StunHeader.MAGIC_COOKIE.toByteArray().plus(header.transactionId)
            family = StandardProtocolFamily.INET6
            xAddress = senderAddress.address.xor(magicCookieAndTxnId)
        } else {
            throw IllegalArgumentException("Unrecognized address format: ${senderAddress.hostAddress}")
        }
        val attributeValue = xorMappedAddressValueSerializer.serializeValue(
            family = family,
            xPort = xPort,
            xAddress = xAddress,
        )
        return StunAttribute(
            type = StunAttributeType.XOR_MAPPED_ADDRESS,
            valueLength = attributeValue.size.toUShort(),
            value = attributeValue,
            offset = -1
        )
    }

    private fun getMagicCookieTopTwoBytesAsInt(): Int {
        val magicCookieBytes = StunHeader.MAGIC_COOKIE.toByteArray()
        var xorInt = magicCookieBytes[0].toInt()
        xorInt = xorInt shl 8
        xorInt = xorInt or magicCookieBytes[1].toInt()
        return xorInt
    }

    private fun buildResponseHeader(request: StunMessage, attributes: List<StunAttribute>, hasFingerprint: Boolean): StunHeader {
        var messageLength = attributes.sumOf { it.getLengthBytes() }
        if (hasFingerprint) {
            messageLength += FINGERPRINT_VALUE_LENGTH_BYTES.toInt() + StunAttribute.ATTRIBUTE_HEADER_SIZE_BYTES
        }
        return StunHeader(
            messageType = StunMessageType.BINDING_RESPONSE,
            messageLength = messageLength.toUShort(),
            magicCookie = StunHeader.MAGIC_COOKIE,
            transactionId = request.header.transactionId
        )
    }

    private fun setFingerprintValue(buffer: ByteBuffer, fingerprintAttr: StunAttribute) {
        val crc32 = CRC32()
        val filledArray = buffer.array().sliceArray(IntRange(0, buffer.arrayOffset()))
        crc32.update(filledArray)
        val result = crc32.value
        System.arraycopy(result, 0, fingerprintAttr.value, 0, fingerprintAttr.value.size)
    }
}