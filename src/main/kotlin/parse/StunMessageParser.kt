package com.break2bits.parse

import com.break2bits.message.StunMessage
import java.net.InetAddress
import java.nio.ByteBuffer

class StunMessageParser(
    private val stunHeaderParser: StunHeaderParser,
    private val stunAttributesParser: StunAttributesParser
) {
    fun parse(messageData: ByteArray, senderAddress: InetAddress, senderPort: Int): StunMessage {
        val buff = ByteBuffer.wrap(messageData)
        val header = stunHeaderParser.parse(buff)
        validateMessageLength(header.messageLength, buff.remaining())
        val attributes = stunAttributesParser.parse(buff, header.messageLength)
        return StunMessage(
            header = header,
            attributes = attributes,
            bytes = messageData,
        )
    }

    private fun validateMessageLength(messageLength: UShort, remainingBytes: Int) {
        if (messageLength.toInt() != remainingBytes) {
            throw StunParseException("Header message length $messageLength value does not match the actual remaining number of message bytes $remainingBytes")
        }
    }
}