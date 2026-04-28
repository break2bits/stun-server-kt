package com.break2bits.message.attribute

enum class StunAttributeType(val value: UShort) {
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
        public val COMPREHENSION_REQUIRED_TYPES = setOf(
            MAPPED_ADDRESS, USERNAME, MESSAGE_INTEGRITY, ERROR_CODE, UNKNOWN_ATTRIBUTES, REALM, NONCE, XOR_MAPPED_ADDRESS
        )
        public val COMPREHENSION_OPTIONAL_TYPES = setOf(SOFTWARE, ALTERNATE_SERVER, FINGERPRINT)

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