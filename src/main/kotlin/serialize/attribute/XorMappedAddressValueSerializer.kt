package com.break2bits.serialize.attribute

import java.net.StandardProtocolFamily
import java.nio.ByteBuffer

class XorMappedAddressValueSerializer {
    companion object {
        const val IPV4_ADDRESS_SIZE_BYTES = 4
        const val IPV6_ADDRESS_SIZE_BYTES = 16
        const val EXTRA_ATTRIBUTE_VALUE_BYTES = 4

        private const val IPV4_ATTRIBUTE_VALUE_SIZE_BYTES = IPV4_ADDRESS_SIZE_BYTES + EXTRA_ATTRIBUTE_VALUE_BYTES
        private const val IPV6_ATTRIBUTE_VALUE_SIZE_BYTES = IPV6_ADDRESS_SIZE_BYTES + EXTRA_ATTRIBUTE_VALUE_BYTES
    }

    fun serializeValue(family: StandardProtocolFamily, xPort: Int, xAddress: ByteArray): ByteArray {
        val toAllocate: Int
        if (family == StandardProtocolFamily.INET) {
            toAllocate = IPV4_ATTRIBUTE_VALUE_SIZE_BYTES
        } else {
            toAllocate = IPV6_ATTRIBUTE_VALUE_SIZE_BYTES
        }
        val buffer = ByteBuffer.allocate(toAllocate)
        buffer.put(0b0)
        buffer.put(xPort.toByte())
        buffer.put(xAddress)
        return buffer.array()
    }
}