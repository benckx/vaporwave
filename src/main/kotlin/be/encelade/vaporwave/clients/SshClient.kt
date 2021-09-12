package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshConnection
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream

internal class SshClient(private val username: String,
                         private val password: String,
                         private val host: String,
                         private val port: Int = 22) {

    constructor(conn: SshConnection) : this(conn.username, conn.password, conn.host, conn.port)

    fun isReachable(): Boolean {
        return try {
            val session = JSch().getSession(username, host, port)
            session.setPassword(password)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect()
            session.disconnect()
            true
        } catch (t: Throwable) {
            false
        }
    }

    fun sendCommand(command: String): String {
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

}
