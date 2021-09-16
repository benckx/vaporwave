package be.encelade.vaporwave.gui.api

import be.encelade.vaporwave.model.devices.Device

interface DeviceSelectionCallback {

    fun noDeviceSelected()

    fun offlineDeviceSelected(device: Device)

    fun onlineDeviceSelected(device: Device)

}
