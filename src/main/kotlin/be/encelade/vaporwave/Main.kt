package be.encelade.vaporwave

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream

fun main() {
    val command = " ls -l --time-style=full-iso /roms/*/*.{srm,state}"
    val result = sendCommand("ark", "ark", "192.168.178.57", 22, command)
    println(result)
}

fun sendCommand(username: String, password: String, host: String, port: Int, command: String): String {
    var session: Session? = null
    var channel: ChannelExec? = null
    try {
        session = JSch().getSession(username, host, port)
        session.setPassword(password)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect() // send Exception if offline
        channel = session.openChannel("exec") as ChannelExec
        channel.setCommand(command)
        val responseStream = ByteArrayOutputStream()
        channel.outputStream = responseStream
        channel.connect()
        while (channel.isConnected) {
            Thread.sleep(100)
        }
        return String(responseStream.toByteArray())
    } finally {
        session?.disconnect()
        channel?.disconnect()
    }
}
