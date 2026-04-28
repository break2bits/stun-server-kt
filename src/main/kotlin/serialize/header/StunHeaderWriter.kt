package com.break2bits.serialize.header

import com.break2bits.binary.put
import com.break2bits.message.header.StunHeader
import java.nio.ByteBuffer

class StunHeaderWriter {
    fun write(buffer: ByteBuffer, header: StunHeader) {
        buffer
            .put(header.messageType.value)
            .put(header.messageLength)
            .put(header.magicCookie)
            .put(header.transactionId)
    }
}