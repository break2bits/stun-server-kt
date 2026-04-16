package com.break2bits.message.header

enum class StunMessageType(val value: UShort) {
    BINDING_REQUEST(0b000000000001u),
    INDICATION_REQUEST(0b000000010001u),
    BINDING_RESPONSE(0b000100000001u),
    ERROR_RESPONSE(0b000100010001u);

    companion object {
        fun fromBinary(value: UShort): StunMessageType {
            entries.forEach {
                if (it.value == value) {
                    return it
                }
            }
            throw IllegalArgumentException("Unexpected value for stun message type: $value")
        }
    }
}
