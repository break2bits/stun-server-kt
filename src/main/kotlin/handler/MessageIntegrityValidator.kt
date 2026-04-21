package com.break2bits.handler

import com.break2bits.message.StunAttributeAndPosition
import com.break2bits.message.StunMessage
import com.break2bits.message.attribute.StunAttributeType
import javax.crypto.Mac

class MessageIntegrityValidator {
    fun validate(message: StunMessage) {
        val usernameAttrAndPos = getRequiredAttribute(message, StunAttributeType.USERNAME)
        val messageIntegAttrAndPos = getRequiredAttribute(message, StunAttributeType.MESSAGE_INTEGRITY)

        validateMessageIntegrity(message, usernameAttrAndPos, messageIntegAttrAndPos)
    }

    private fun getRequiredAttribute(message: StunMessage, attributeType: StunAttributeType): StunAttributeAndPosition {
        val attribute = message.attributesByType[attributeType]
        if (attribute == null) {
            throw StunRequestException("Required ${attributeType.name} attribute not found")
        }
        return attribute
    }

    private fun validateMessageIntegrity(message: StunMessage, usernameAttrAndPos: StunAttributeAndPosition, messageIntegAttrAndPos: StunAttributeAndPosition) {
        val mac = Mac.getInstance("HmacSHA1")
        // mac.init(key) set the key on the mac using SASL password
        mac.update(message.bytes, 0, messageIntegAttrAndPos.attribute.offset)


        val hmac = mac.doFinal()
    }
}