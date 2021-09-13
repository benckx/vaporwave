package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshDevice

class SshDeviceClient(device: SshDevice) : DeviceClient<SshDevice>(device) {

    private val sshClient = SshClient(device.conn)

    override fun isReachable(): Boolean {
        return sshClient.isReachable()
    }

    override fun listRomFolderFiles(): String {
        val command = "ls -l --time-style=full-iso /roms/*/*.*"
        return sshClient.sendCommand(command)
    }

}
