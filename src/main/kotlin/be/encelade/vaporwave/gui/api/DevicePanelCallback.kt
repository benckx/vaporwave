package be.encelade.vaporwave.gui.api

interface DevicePanelCallback {

    fun deviceSelected(idx: Int)

    fun addDeviceButtonClicked()

    fun refreshDevicesButtonClicked()

    fun unSelectDeviceButtonClicked()

}
