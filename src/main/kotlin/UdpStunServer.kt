package com.break2bits

import com.break2bits.parse.StunMessageParser
import java.net.DatagramPacket
import java.net.DatagramSocket

/*
Stun header is 20 bytes
Stun message is variable length
 */
class UdpStunServer(
    private val port: Int,
    private val stunMessageParser: StunMessageParser
) {
    private val socket = DatagramSocket(port)

    fun start() {
        socket.use { activeSocket ->
            while (true) {
                val buffer = ByteArray(512)
                val datagram = DatagramPacket(buffer, buffer.size)
                activeSocket.receive(datagram)

            }
        }
    }
}