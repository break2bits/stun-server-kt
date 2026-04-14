package com.break2bits.message.attribute

data class StunAttribute(
    val type: StunAttributeType,
    val length: UShort, // length of value
    val value: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StunAttribute

        if (type != other.type) return false
        if (length != other.length) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + length.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}