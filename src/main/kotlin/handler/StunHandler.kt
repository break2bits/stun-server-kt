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
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.zip.CRC32

class StunHandler(
    private val messageIntegrityValidator: MessageIntegrityValidator
) {
    private companion object {
        private const val IPV4_ADDRESS_SIZE_BYTES = 4
        private const val IPV6_ADDRESS_SIZE_BYTES = 16
    }

    fun handle(message: StunMessage, senderAddress: InetAddress, senderPort: Int): StunMessage? {
        validateMessageType(message.header)

        // if indication, no response is necessary
        val responseMessageType = StunMessageType.BINDING_RESPONSE

        val hadFingerprint = maybeValidateFingerprintAttribute(message)
        val xorMappedAddressAttr = createXorMappedAddressAttribute(senderAddress, senderPort)
        // authentication is optional
        // messageIntegrityValidator.validate(message)
        // check for fingerprint
        // add xor mapped address
        // add software attribute

        throw NotImplementedError("Message processing not yet implemented")
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

    private fun createXorMappedAddressAttribute(senderAddress: InetAddress, senderPort: Int): StunAttribute {
        val magicCookieTopTwoBytes = getMagicCookieTopTwoBytesAsInt()
        val xPort = senderPort xor magicCookieTopTwoBytes

        var xAddress: ByteArray
        if (senderAddress.address.size == IPV6_ADDRESS_SIZE_BYTES) {
            // IPV4 xAddress calculation

            xAddress = senderAddress.address.xor(StunHeader.MAGIC_COOKIE.toByteArray())
        } else if (senderAddress.address.size == IPV6_ADDRESS_SIZE_BYTES) {
            // IPV6 xAddress calculation
            
        } else {
            throw IllegalArgumentException("Unrecognized address format: ${senderAddress.hostAddress}")
        }
    }

    private fun getMagicCookieTopTwoBytesAsInt(): Int {
        val magicCookieBytes = StunHeader.MAGIC_COOKIE.toByteArray()
        var xorInt = magicCookieBytes[0].toInt()
        xorInt = xorInt shl 8
        xorInt = xorInt or magicCookieBytes[1].toInt()
        return xorInt
    }
}