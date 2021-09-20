package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshDevice
import be.encelade.vaporwave.services.ExtensionMap.saveFilesExtension
import java.io.File

class SshDeviceClient(device: SshDevice) : DeviceClient<SshDevice>(device) {

    private val sshClient = SshClient(device.conn)

    override fun consoleFolder(console: String): String {
        return "/roms/$console/"
    }

    override fun isReachable(): Boolean {
        return sshClient.isReachable()
    }

    override fun lsCommandRomFolder(): String {
        // TODO: use consoleFolder?
        val command = "ls -l --time-style=full-iso /roms/*/*.*"
        return sshClient.sendCommand(command)
    }

    override fun md5sumCommandRomFolder(): String {
        // TODO: use consoleFolder?
        val command = "/roms/*/*.{${saveFilesExtension.joinToString(",")}}"
        return sshClient.sendCommand(command)
    }

    override fun downloadFilesFromDevice(filePairs: List<Pair<String, File>>): List<File> {
        return sshClient.downloadFiles(filePairs)
    }

    override fun uploadFilesToDevice(filePairs: List<Pair<File, String>>) {
        sshClient.uploadFiles(filePairs)
//        sshClient.sendCommand(buildUpdateLastModifiedCommand(filePairs))
    }

}
