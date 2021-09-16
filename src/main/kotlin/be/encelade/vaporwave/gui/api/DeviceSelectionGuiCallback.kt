package be.encelade.vaporwave.gui.api

import be.encelade.vaporwave.model.devices.Device

interface DeviceSelectionGuiCallback {

    fun noDeviceSelected()

    fun offlineDeviceSelected(device: Device)

    fun onlineDeviceSelected(device: Device)

}
