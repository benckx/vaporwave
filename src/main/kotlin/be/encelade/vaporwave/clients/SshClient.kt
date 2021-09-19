package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshConnection
import be.encelade.vaporwave.utils.CollectionUtils.exists
import be.encelade.vaporwave.utils.LazyLogging
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.File.separator

internal class SshClient(private val username: String,
                         private val password: String,
                         private val host: String,
                         private val port: Int = 22) : LazyLogging {

    constructor(conn: SshConnection) : this(conn.username, conn.password, conn.host, conn.port)

    private val timeoutMillis = 5 * 1000

    fun isReachable(): Boolean {
        return try {
            val session = buildSession()
            session.connect(timeoutMillis) // send Exception if offline
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
            if (command.contains("\n")) {
                logger.debug("sending command:\n$command")
            } else {
                logger.debug("sending command: $command")
            }

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

    /**
     * @param filePairs [Pair] of <sourceFilePath, targetFolder>
     */
    fun downloadFiles(filePairs: List<Pair<String, File>>): List<File> {
        if (filePairs.exists { (_, folder) -> !folder.exists() || !folder.isDirectory }) {
            throw IllegalArgumentException()
        }

        val result = mutableListOf<File>()

        openSftpChannel { channel ->
            filePairs.forEach { (sourceFilePath, targetFolder) ->
                val fileName = sourceFilePath.split("/").last()
                val targetFilePath = "$targetFolder$separator$fileName"
                logger.debug("downloading $sourceFilePath to $targetFilePath...")
                channel.get(sourceFilePath, targetFilePath)
                result += File(targetFilePath)
            }
        }

        return result
    }

    fun uploadFiles(filePairs: List<Pair<File, String>>) {
        openSftpChannel { channel ->
            filePairs.forEach { (file, targetFilePath) ->
                logger.debug("uploading '${file.absolutePath}' to '$targetFilePath'...")
                channel.put(file.absolutePath, targetFilePath)
//                channel.setMtime(targetFilePath, (file.lastModified() / 1000).toInt())
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
            session.connect(timeoutMillis)
            channel = session.openChannel(channelType) as C
            if (channelType == "sftp") {
                channel.connect(timeoutMillis)
            }
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
