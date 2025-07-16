package com.ark.arkpad.ui.screens.controller

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.SocketException

const val READ_TIMEOUT = 5000

private enum class Message(val type: UByte) {
    CONNECT(1u),
    DISCONNECT(2u),
    DATA(4u),
    PING(8u),
}

private enum class Response(val code: UByte) {
    CONNECTION_SUCCESS(100u),
    CONNECTION_FAILURE(101u),
    DISCONNECTED(200u),
    UNSUPPORTED(201u),
    PONG(202u);

    companion object {
        fun tryFrom(code: UByte): Response {
            return Response.entries
                .firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Response code is invalid.")
        }
    }
}

class ClientException(message: String) : Exception(message, null)

class NetworkClient(
    private val host: String,
    private val port: Int,
) {
    private val socket = DatagramSocket()

    init {
        socket.reuseAddress = true
        socket.soTimeout = READ_TIMEOUT
    }

    val isConnected: Boolean
        get() = socket.isConnected

    private fun send(message: Message, payload: ByteArray?) {
        var data = byteArrayOf(message.type.toByte())
        if (payload != null) {
            data += payload
        }
        val packet = DatagramPacket(data, data.size)
        try {
            socket.send(packet)
        } catch (e: Exception) {
            when (e) {
                is SocketException,
                is IOException -> throw ClientException("Not connected to the network.")

                else -> throw ClientException("An unexpected error occurred, ${e.message}.")
            }
        }
    }

    private fun receive(): Response {
        val buffer = ByteArray(1)
        val packet = DatagramPacket(buffer, buffer.size)
        try {
            socket.receive(packet)
        } catch (_: Exception) {
            throw ClientException("No response received from device.")
        }
        val responseCode = buffer.first().toUByte()
        return Response.tryFrom(responseCode)
    }

    fun connect() {
        if (isConnected) {
            throw ClientException("Already connected to the device.")
        }
        val address = Inet4Address.getByName(host)
        socket.connect(address, port)
        send(Message.CONNECT, byteArrayOf(0x45, 0x45))
        when (receive()) {
            Response.CONNECTION_SUCCESS -> Unit
            Response.CONNECTION_FAILURE -> throw ClientException("Connection refused by device.")
            else -> throw IllegalStateException()
        }
    }

    fun emit(payload: ByteArray) {
        if (!isConnected) {
            throw ClientException("Cannot emit, not connected to the device.")
        }
        send(Message.DATA, payload)
    }

    fun disconnect() {
        if (!isConnected) {
            throw ClientException("Cannot disconnect, not connected to device.")
        }
        send(Message.DISCONNECT, null)
        if (receive() != Response.DISCONNECTED) {
            throw ClientException("Did not receive the expected disconnect confirmation.")
        }
        socket.disconnect()
    }

    fun ping() {
        if (!isConnected) {
            throw ClientException("Cannot ping, not connected to device.")
        }
        send(Message.PING, null)
        if (receive() != Response.PONG) {
            throw ClientException("Did not receive the expected ping response.")
        }
    }
}