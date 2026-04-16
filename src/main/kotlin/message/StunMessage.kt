package com.break2bits.message

import com.break2bits.message.attribute.StunAttribute
import com.break2bits.message.header.StunHeader

data class StunMessage(
    val header: StunHeader,
    val attributes: List<StunAttribute>
)
