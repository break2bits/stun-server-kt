package com.break2bits.cli

class IntArgDefinition(
    shortKey: String,
    longKey: String,
    private val default: Int? = null,
) : ArgDefinition(shortKey, longKey) {
    fun getDefault(): Int? {
        return default
    }

    override fun toString(): String {
        return "$shortKey $longKey (int)"
    }
}