package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshConnection
import be.encelade.vaporwave.utils.LazyLogging
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.ByteArrayOutputStream
import java.io.File

internal class SshClient(private val username: String,
                         private val password: String,
                         private val host: String,
                         private val port: Int = 22) : LazyLogging {

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

    fun downloadFiles(filePaths: List<String>, targetFolder: String): List<File> {
        val result = mutableListOf<File>()
        var session: Session? = null
        var channel: ChannelSftp? = null
        try {
            session = JSch().getSession(username, host, port)
            session.setPassword(password)
            session.setConfig("StrictHostKeyChecking", "no")
            session.connect() // send Exception if offline
            channel = session.openChannel("sftp") as ChannelSftp
            filePaths.forEach { filePath ->
                val fileName = filePath.split("/").last()
                val targetFilePath = "$targetFolder${File.separator}/$fileName"
                logger.debug("downloading $filePath to $targetFilePath...")
                channel.get(filePath, targetFilePath)
                result += File(targetFilePath)
            }
        } finally {
            session?.disconnect()
            channel?.disconnect()
        }
        return result
    }

}
