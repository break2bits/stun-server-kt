package com.break2bits.message

import com.break2bits.message.attribute.StunAttribute
import com.break2bits.message.header.StunHeader

data class StunMessage(
    val header: StunHeader,
    val attributes: List<StunAttribute>,
    val bytes: ByteArray,
) {
    val attributesByType = attributes.mapIndexed { idx, attr ->
        attr.type to StunAttributeAndPosition(attr, idx)
    }.toMap()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StunMessage

        if (header != other.header) return false
        if (attributes != other.attributes) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (attributesByType != other.attributesByType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + attributesByType.hashCode()
        return result
    }
}

data class StunAttributeAndPosition(
    val attribute: StunAttribute,
    val position: Int
)