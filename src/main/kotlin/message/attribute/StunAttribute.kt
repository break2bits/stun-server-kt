package com.break2bits.message.attribute

data class StunAttribute(
    val type: StunAttributeType,
    val valueLength: UShort, // length of value
    val value: ByteArray,
    val offset: Int, // byte offset from start of Stun message
) {
    companion object {
        const val ATTRIBUTE_HEADER_SIZE_BYTES = 2
    }

    fun getLengthBytes(): Int {
        return ATTRIBUTE_HEADER_SIZE_BYTES + valueLength.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StunAttribute

        if (type != other.type) return false
        if (valueLength != other.valueLength) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + valueLength.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}