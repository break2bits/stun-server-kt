package com.break2bits.parse

import com.break2bits.message.header.StunHeader
import com.break2bits.message.header.StunMessageType
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class StunHeaderParser {
    fun parse(buff: ByteBuffer): StunHeader {
        try {
            val messageTypeData: UShort = buff.getShort().toUShort();
            val messageType = parseMessageType(messageTypeData);

            val messageLength = buff.getShort().toUShort();
            validateMessageLength(messageLength);

            val magicCookie = buff.getInt().toUInt();
            validateMagicCookie(magicCookie);

            val transactionId = ByteArray(12)
            buff.get(transactionId)

            return StunHeader(
                messageType = messageType,
                messageLength = messageLength,
                magicCookie = magicCookie,
                transactionId = transactionId
            )
        } catch (e: BufferUnderflowException) {
            throw StunParseException("Message did not contain enough bytes to parse Stun header", e)
        }
    }

    private fun parseMessageType(messageTypeData: UShort): StunMessageType {
        try {
            return StunMessageType.fromBinary(messageTypeData)
        } catch (e: IllegalArgumentException) {
            throw StunParseException("Failed to parse header message type", e)
        }
    }

    private fun validateMessageLength(messageLength: UShort) {
        if (messageLength.mod(4u) != 0u) {
            throw StunParseException("Invalid message length, must be a multiple of 4: $messageLength");
        }
    }

    private fun validateMagicCookie(magicCookie: UInt) {
        if (magicCookie != StunHeader.MAGIC_COOKIE) {
            throw StunParseException("Invalid magic cookie: $magicCookie")
        }
    }
}