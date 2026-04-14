package com.break2bits

import com.break2bits.parse.StunMessageParser
import com.break2bits.serialize.StunMessageSerializer
import java.net.DatagramPacket
import java.net.DatagramSocket

/*
Stun header is 20 bytes
Stun message is variable length
 */
class UdpStunServer(
    private val port: Int,
    private val stunMessageParser: StunMessageParser,
    private val stunHandler: StunHandler,
    private val stunMessageSerializer: StunMessageSerializer
) {
    private val socket = DatagramSocket(port)

    fun start() {
        socket.use { activeSocket ->
            while (true) {
                val buffer = ByteArray(512)
                val requestDatagram = DatagramPacket(buffer, buffer.size)
                activeSocket.receive(requestDatagram)

                // parse and validate the raw binary data
                val request = stunMessageParser.parse(requestDatagram.data, requestDatagram.address, requestDatagram.port)

                // create a response based on the input request
                val response = stunHandler.handle(request)

                // serialize the response
                val responseData = stunMessageSerializer.serialize(response)

                // wrap the response in a UDP datagram addressed to the sender
                val responseDatagram = DatagramPacket(responseData, responseData.size, requestDatagram.address, requestDatagram.port)

                // send the datagram
                activeSocket.send(responseDatagram)
            }
        }
    }
}