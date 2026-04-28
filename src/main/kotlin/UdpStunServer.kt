package com.break2bits

import com.break2bits.handler.StunHandler
import com.break2bits.parse.StunMessageParser
import java.net.DatagramPacket
import java.net.DatagramSocket

/*
Stun header is 20 bytes
Stun message is variable length

What if instead of serializing and deserializing we just deal in bytes

1. Every part of the stun message has an offset, a length and maybe a memoized deserialized value
 */
class UdpStunServer(
    private val port: Int,
    private val stunMessageParser: StunMessageParser,
    private val stunHandler: StunHandler
) {
    private val socket = DatagramSocket(port)

    fun start() {
        socket.use { activeSocket ->
            while (true) {
                val buffer = ByteArray(512)
                val requestDatagram = DatagramPacket(buffer, buffer.size)
                activeSocket.receive(requestDatagram)

                // parse and validate the raw binary data
                val request = stunMessageParser.parse(requestDatagram.data)

                // create a response based on the input request
                val response = stunHandler.handle(request, requestDatagram.address, requestDatagram.port)
                if (response == null) { // for indications
                    continue
                }

                // wrap the response in a UDP datagram addressed to the sender
                val responseDatagram = DatagramPacket(response.bytes, response.bytes.size, requestDatagram.address, requestDatagram.port)

                // send the datagram
                activeSocket.send(responseDatagram)
            }
        }
    }
}