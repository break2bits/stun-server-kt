package com.break2bits.message.attribute

enum class StunAttributeType(private val value: UShort) {
    // Comprehension required
    MAPPED_ADDRESS(0x0001u),
    USERNAME(0x0006u),
    MESSAGE_INTEGRITY(0x0008u),
    ERROR_CODE(0x0009u),
    UNKNOWN_ATTRIBUTES(0x000Au),
    REALM(0x0014u),
    NONCE(0x0015u),
    XOR_MAPPED_ADDRESS(0x0020u),

    // Comprehension optional
    SOFTWARE(0x8022u),
    ALTERNATE_SERVER(0x8023u),
    FINGERPRINT(0x8028u);

    public companion object {
        fun fromBinary(value: UShort): StunAttributeType {
            entries.forEach {
                if (it.value == value) {
                    return it
                }
            }
            throw IllegalArgumentException("Unrecognized stun attribute type: $value")
        }
    }
}