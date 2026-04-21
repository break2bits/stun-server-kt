package com.break2bits.parse

import com.break2bits.message.attribute.StunAttribute
import com.break2bits.message.attribute.StunAttributeType
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class StunAttributesParser {
    fun parse(buff: ByteBuffer, messageLength: UShort): List<StunAttribute> {
        val attributes = mutableListOf<StunAttribute>()
        try {
            while (buff.arrayOffset() < messageLength.toInt()) {
                val attribute = parseAttribute(buff)
                attributes.add(attribute)
            }

            return attributes
        } catch (e: BufferUnderflowException) {
            throw StunParseException("Message did not contain enough bytes to parse Stun attributes", e)
        }
    }

    private fun parseAttribute(buff: ByteBuffer): StunAttribute {
        val offset = buff.arrayOffset()
        val attributeType = parseAttributeType(buff)
        val attributeLength = buff.getShort().toUShort()
        validateAttributeLength(attributeLength)
        val attributeValue = ByteArray(attributeLength.toInt())
        buff.get(attributeValue)
        return StunAttribute(
            type = attributeType,
            valueLength = attributeLength,
            value = attributeValue,
            offset = offset
        )
    }

    private fun parseAttributeType(buff: ByteBuffer): StunAttributeType {
        val attributeTypeData = buff.getShort()
        try {
            val attributeType = StunAttributeType.fromBinary(attributeTypeData.toUShort())
            return attributeType
        } catch (e: IllegalArgumentException) {
            throw StunParseException("Invalid attribute type: $attributeTypeData", e)
        }
    }

    private fun validateAttributeLength(length: UShort) {
        if (length.mod(4u) != 0u) {
            throw StunParseException("Invalid attribute length, must be a multiple of 4 bytes: $length")
        }
    }
}