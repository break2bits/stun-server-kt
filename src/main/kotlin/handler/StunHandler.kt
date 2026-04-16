package com.break2bits.handler

import com.break2bits.binary.string.toBinaryStr
import com.break2bits.message.StunMessage
import com.break2bits.message.header.StunHeader
import com.break2bits.message.header.StunMessageType

class StunHandler {
    fun handle(message: StunMessage): StunMessage {
        validateMessageType(message.header)

        val responseMessageType = StunMessageType.BINDING_RESPONSE
        

        throw NotImplementedError("Message processing not yet implemented")
    }

    private fun validateMessageType(message: StunHeader) {
        if (message.messageType != StunMessageType.BINDING_REQUEST) {
            val binStr = StunMessageType.BINDING_REQUEST.value.toBinaryStr()
            throw StunRequestException("Server only serves BINDING requests of message type $binStr")
        }
    }
}