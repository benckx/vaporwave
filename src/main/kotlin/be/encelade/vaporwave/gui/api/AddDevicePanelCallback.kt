package be.encelade.vaporwave.gui.api

import be.encelade.vaporwave.model.devices.SshConnection

interface AddDevicePanelCallback {

    fun testConnectionButtonClicked(sshConnection: SshConnection)

    fun addDeviceButtonClicked(name: String, sshConnection: SshConnection)

}
