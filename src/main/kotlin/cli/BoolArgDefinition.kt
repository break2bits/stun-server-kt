package com.break2bits.cli

class BoolArgDefinition(
    shortKey: String,
    longKey: String,
    private val default: Boolean? = null
) : ArgDefinition(shortKey, longKey) {
    fun getDefault(): Boolean? {
        return default
    }

    override fun toString(): String {
        return "$shortKey $longKey"
    }
}