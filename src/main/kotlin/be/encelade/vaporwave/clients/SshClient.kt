package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshConnection
import be.encelade.vaporwave.utils.LazyLogging
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

internal class SshClient(private val username: String,
                         private val password: String,
                         private val host: String,
                         private val port: Int = 22) : LazyLogging {

    constructor(conn: SshConnection) : this(conn.username, conn.password, conn.host, conn.port)

    fun isReachable(): Boolean {
        return try {
            val session = buildSession()
            session.connect() // send Exception if offline
            session.disconnect()
            true
        } catch (t: Throwable) {
            logger.error("failed to open session: " + t.message)
            false
        }
    }

    fun sendCommand(command: String): String {
        val responseStream = ByteArrayOutputStream()

        openExecChannel { channel ->
            channel.setCommand(command)
            channel.outputStream = responseStream
            channel.connect()
            while (channel.isConnected) {
                Thread.sleep(100)
            }
        }

        responseStream.close()
        return String(responseStream.toByteArray())
    }

    fun downloadFiles(filePaths: List<String>, targetFolder: String): List<File> {
        val result = mutableListOf<File>()

        openSftpChannel { channel ->
            filePaths.forEach { filePath ->
                val fileName = filePath.split("/").last()
                val targetFilePath = "$targetFolder${File.separator}/$fileName"
                logger.debug("downloading $filePath to $targetFilePath...")
                channel.get(filePath, targetFilePath)
                result += File(targetFilePath)
            }
        }

        return result
    }

    fun uploadFiles(files: List<Pair<File, String>>) {
        openSftpChannel { channel ->
            files.forEach { (file, targetFilePath) ->
                logger.debug("uploading ${file.absolutePath} to $targetFilePath...")
                channel.put(FileInputStream(file), targetFilePath)
            }
        }
    }

    private fun openExecChannel(block: (ChannelExec) -> Unit) {
        openChannel("exec", block)
    }

    private fun openSftpChannel(block: (ChannelSftp) -> Unit) {
        openChannel("sftp", block)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <C : Channel> openChannel(channelType: String, block: (C) -> Unit) {
        var session: Session? = null
        var channel: C? = null
        try {
            session = buildSession()
            session.connect()
            channel = session.openChannel(channelType) as C
            block(channel)
        } finally {
            session?.disconnect()
            channel?.disconnect()
        }
    }

    private fun buildSession(): Session {
        val session = JSch().getSession(username, host, port)
        session.setPassword(password)
        session.setConfig("StrictHostKeyChecking", "no")
        return session
    }

}
