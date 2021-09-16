package be.encelade.vaporwave.gui.api

import be.encelade.vaporwave.model.devices.Device

internal interface DeviceSelectionGuiCallback {

    fun noDeviceSelected()

    fun offlineDeviceSelected(device: Device)

    fun onlineDeviceSelected(device: Device)

}
