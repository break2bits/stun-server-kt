package com.break2bits.serialize.attribute

import com.break2bits.binary.put
import com.break2bits.message.attribute.StunAttribute
import java.nio.ByteBuffer

class StunAttributeWriter {
    fun write(buffer: ByteBuffer, attribute: StunAttribute) {
        buffer
            .put(attribute.type.value)
            .put(attribute.valueLength)
            .put(attribute.value)
    }
}