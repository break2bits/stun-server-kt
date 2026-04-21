package com.break2bits.handler

import com.break2bits.binary.string.toBinaryStr
import com.break2bits.message.StunMessage
import com.break2bits.message.header.StunHeader
import com.break2bits.message.header.StunMessageType

class StunHandler(
    private val messageIntegrityValidator: MessageIntegrityValidator
) {
    fun handle(message: StunMessage): StunMessage? {
        validateMessageType(message.header)

        // if indication, no response is necessary
        val responseMessageType = StunMessageType.BINDING_RESPONSE

        messageIntegrityValidator.validate(message)

        throw NotImplementedError("Message processing not yet implemented")
    }

    private fun validateMessageType(message: StunHeader) {
        if (message.messageType != StunMessageType.BINDING_REQUEST) {
            val binStr = StunMessageType.BINDING_REQUEST.value.toBinaryStr()
            throw StunRequestException("Server only serves BINDING requests of message type $binStr")
        }
    }


}