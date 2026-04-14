package com.break2bits.message

import com.break2bits.message.attribute.StunAttribute
import com.break2bits.message.header.StunHeader

data class StunMessage(
    private val header: StunHeader,
    private val attributes: List<StunAttribute>
)
