package com.break2bits.binary

import kotlin.experimental.xor

// xor two byte arrays, storing the result in a new bytearray
fun ByteArray.xor(other: ByteArray): ByteArray {
    val result = ByteArray(size)
    forEachIndexed { idx, value ->
        val otherVal = other[idx]
        result[idx] = value.xor(otherVal)
    }
    return result
}