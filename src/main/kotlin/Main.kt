package com.break2bits

import com.break2bits.cli.ArgDefinition
import com.break2bits.cli.BoolArgDefinition
import com.break2bits.cli.IntArgDefinition
import com.break2bits.cli.StunServerArgParser
import com.break2bits.handler.MessageIntegrityValidator
import com.break2bits.handler.StunHandler
import com.break2bits.parse.StunAttributesParser
import com.break2bits.parse.StunHeaderParser
import com.break2bits.parse.StunMessageParser
import com.break2bits.serialize.attribute.StunAttributeWriter
import com.break2bits.serialize.attribute.XorMappedAddressValueSerializer
import com.break2bits.serialize.header.StunHeaderWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val helpArg = BoolArgDefinition("-h", "--help", default = false)
    val portArg = IntArgDefinition("-p", "--port", default = 3478)
    val threadsArg = IntArgDefinition("-n", "--num-threads", default = 1)
    val udpEnabledArg = BoolArgDefinition("-u", "--udp", default = false)
    val tcpEnabledArg = BoolArgDefinition("-t", "--tcp", default = false)

    val argDefs = listOf(helpArg, portArg, threadsArg, udpEnabledArg, tcpEnabledArg)

    try {
        val argParser = StunServerArgParser(args)

        if (argParser.getBool(helpArg)) {
            println(usage(argDefs))
            exitProcess(0)
        }

        val port = argParser.getInt(portArg)
        if (port < 1) {
            throw IllegalArgumentException("Value of $portArg must be greater than or equal to 1, got $port")
        }

        val threads = argParser.getInt(threadsArg)
        if (port < 1) {
            throw IllegalArgumentException("Value of $threadsArg must be greater than or equal to 1, got $threads")
        }

        val udpEnabled = argParser.getBool(udpEnabledArg)

        val tcpEnabled = argParser.getBool(tcpEnabledArg)

        if (!tcpEnabled && !udpEnabled) {
            throw IllegalArgumentException("Either $tcpEnabledArg or $udpEnabledArg must be set")
        }

        println("Starting Stun server with the following config:\nport: $port\nthreads: $threads\nudpEnabled: $udpEnabled\ntcpEnabled: $tcpEnabled")

        val stunServer = createStunServer(port, udpEnabled, tcpEnabled)
        stunServer.start()
    } catch (exception: IllegalArgumentException) {
        println("Failed to parse args: ${exception.message}\n")
        println(usage(argDefs))
        exitProcess(-1)
    }
}

private fun usage(argDefs: List<ArgDefinition>): String {
    return """
        Usage: java -jar stun-server-kt.jar --port 8000 --udp

        ${argDefs.joinToString("\n        ")}
    """.trimIndent()
}

private fun createStunServer(port: Int, udpEnabled: Boolean, tcpEnabled: Boolean): UdpStunServer {
    if (udpEnabled) {
        return UdpStunServer(
            port = port,
            stunMessageParser = StunMessageParser(
                stunHeaderParser = StunHeaderParser(),
                stunAttributesParser = StunAttributesParser()
            ),
            stunHandler = StunHandler(
                messageIntegrityValidator = MessageIntegrityValidator(),
                xorMappedAddressValueSerializer = XorMappedAddressValueSerializer(),
                stunHeaderWriter = StunHeaderWriter(),
                stunAttributeWriter = StunAttributeWriter(),
            )
        )
    }
    throw NotImplementedError("TCP stun server not yet implemented");
}