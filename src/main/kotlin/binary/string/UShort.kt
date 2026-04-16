package com.break2bits.binary.string

import kotlin.UShort

fun UShort.toBinaryStr(): String {
    val intBinStr = Integer.toBinaryString(this.toInt())
    val shortBinStr = intBinStr.substring(intBinStr.length - 16, intBinStr.length)
    return "0b$shortBinStr"
}