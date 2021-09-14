package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshDevice
import java.io.File

class SshDeviceClient(device: SshDevice) : DeviceClient<SshDevice>(device) {

    private val sshClient = SshClient(device.conn)

    override fun isReachable(): Boolean {
        return sshClient.isReachable()
    }

    override fun listRomFolderFiles(): String {
        val command = "ls -l --time-style=full-iso /roms/*/*.*"
        return sshClient.sendCommand(command)
    }

    override fun downloadFiles(filePaths: List<String>, targetFolder: String): List<File> {
        return sshClient.downloadFiles(filePaths, targetFolder)
    }

}
