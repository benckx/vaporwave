package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.SshDevice

class SshDeviceClient(device: SshDevice) : DeviceClient<SshDevice>(device) {

    private val delegate = SshClient(device.conn)

    override fun isReachable(): Boolean {
        return delegate.isReachable()
    }

    override fun listRomFolderFiles(): String {
        val command = "ls -l --time-style=full-iso /roms/*/*"
        return delegate.sendCommand(command)
    }

}
