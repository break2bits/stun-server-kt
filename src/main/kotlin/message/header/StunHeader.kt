package com.break2bits.message.header

data class StunHeader(
    val messageType: StunMessageType,
    val messageLength: UShort,
    val magicCookie: UInt,
    val transactionId: ByteArray,
) {
    public companion object {
        public const val MAGIC_COOKIE: UInt = 0x2112A442u
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StunHeader

        if (messageType != other.messageType) return false
        if (messageLength != other.messageLength) return false
        if (magicCookie != other.magicCookie) return false
        if (!transactionId.contentEquals(other.transactionId)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageType.hashCode()
        result = 31 * result + messageLength.hashCode()
        result = 31 * result + magicCookie.hashCode()
        result = 31 * result + transactionId.contentHashCode()
        return result
    }
}
