package be.encelade.vaporwave.gui.api

interface DevicePanelCallback {

    fun deviceSelected(idx: Int)

    fun refreshDevicesButtonClicked()

    fun unSelectDeviceButtonClicked()

}
