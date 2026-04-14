package com.break2bits.binary

import java.nio.ByteBuffer

fun UInt.toByteArray(): ByteArray {
    val buf = ByteBuffer.allocate(UInt.SIZE_BYTES);
    buf.putInt(this.toInt())
    return buf.array();
}