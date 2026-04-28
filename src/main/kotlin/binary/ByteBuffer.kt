package com.break2bits.binary

import java.nio.ByteBuffer

fun ByteBuffer.put(value: UShort): ByteBuffer {
    putShort(value.toShort())
    return this
}

fun ByteBuffer.put(value: UInt): ByteBuffer {
    putInt(value.toInt())
    return this
}