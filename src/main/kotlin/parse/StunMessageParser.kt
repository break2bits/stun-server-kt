package com.break2bits.parse

import com.break2bits.message.StunMessage
import java.net.InetAddress
import java.nio.ByteBuffer

class StunMessageParser(private val stunHeaderParser: StunHeaderParser) {
    fun parse(messageData: ByteArray, senderAddress: InetAddress, senderPort: Int): StunMessage {
        val buff = ByteBuffer.wrap(messageData)
        val header = stunHeaderParser.parse(buff)
        return StunMessage(
            header = header,
            attributes = listOf()
        )
    }
}