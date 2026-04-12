package com.break2bits

import com.break2bits.cli.ArgDefinition
import com.break2bits.cli.BoolArgDefinition
import com.break2bits.cli.IntArgDefinition
import com.break2bits.cli.StunServerArgParser
import com.sun.tools.javac.tree.TreeInfo.args
import kotlin.system.exitProcess

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(args: Array<String>) {
    val portArg = IntArgDefinition("-p", "--port", default = 3478)
    val threadsArg = IntArgDefinition("-t", "--threads", default = 1)
    val udpEnabledArg = BoolArgDefinition("-u", "--udp", default = false)
    val tcpEnabledArg = BoolArgDefinition("-t", "--tcp", default = false)

    try {
        val argParser = StunServerArgParser(args);

        val port = argParser.getInt(portArg);
        if (port < 1) {
            throw IllegalArgumentException("Value of $portArg must be greater than or equal to 1, got $port");
        }

        val threads = argParser.getInt(threadsArg);
        if (port < 1) {
            throw IllegalArgumentException("Value of $threadsArg must be greater than or equal to 1, got $threads");
        }

        val udpEnabled = argParser.getBool(udpEnabledArg);

        val tcpEnabled = argParser.getBool(tcpEnabledArg);

        if (!tcpEnabled && !udpEnabled) {
            throw IllegalArgumentException("Either $tcpEnabledArg or $udpEnabledArg must be set");
        }

        println("Starting Stun server with the following config:\nport: $port\nthreads: $threads\nudpEnabled: $udpEnabled\ntcpEnabled: $tcpEnabled");
    } catch (exception: IllegalArgumentException) {
        printHelpAndExit(exception, listOf(portArg, threadsArg, udpEnabledArg, tcpEnabledArg))
    }
}

private fun printHelpAndExit(exception: IllegalArgumentException, argDefs: List<ArgDefinition>) {
    println("""
        Failed to parse args: ${exception.message}
        
        Usage: java -jar stun-server-kt.jar --port 8000 --udp
        
        ${argDefs.joinToString("\n        ")}
    """.trimIndent())
    exitProcess(-1)
}