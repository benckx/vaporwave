package be.encelade.vaporwave.clients

class RemoteDeviceClient : DeviceClient {

    private val delegate = SshClient("ark", "ark", "192.168.178.57", 22)

    override fun isReachable(): Boolean {
        return delegate.isReachable()
    }

    override fun listRomFolderFiles(): String {
        val command = "ls -l --time-style=full-iso /roms/*/*.{srm,state}"
        return delegate.sendCommand(command)
    }

}
