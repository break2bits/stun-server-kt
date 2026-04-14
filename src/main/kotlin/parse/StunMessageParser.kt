package com.break2bits.parse

import com.break2bits.message.StunMessage
import java.net.DatagramPacket
import java.nio.ByteBuffer

class StunMessageParser(private val stunHeaderParser: StunHeaderParser) {
    fun parse(datagram: DatagramPacket): StunMessage {
        val buff = ByteBuffer.wrap(datagram.data)
        val header = stunHeaderParser.parse(buff)
        return StunMessage(
            header = header,
            attributes = listOf()
        )
    }
}