package be.encelade.vaporwave.services

import be.encelade.vaporwave.gui.api.ActionButtonCallback
import be.encelade.vaporwave.gui.api.DeviceSelectionGuiCallback
import be.encelade.vaporwave.gui.components.ActionPanel
import be.encelade.vaporwave.gui.components.DeviceListPanel
import be.encelade.vaporwave.gui.components.MainPanel
import be.encelade.vaporwave.gui.components.RomCollectionPanel
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.utils.LazyLogging

class GuiController(private val deviceManager: DeviceManager,
                    private val localRomManager: LocalRomManager,
                    private val saveFilesManager: SaveFilesManager) :
        DeviceSelectionGuiCallback, ActionButtonCallback, LazyLogging {

    private val deviceListPanel = DeviceListPanel(this)
    private val romCollectionPanel = RomCollectionPanel()
    private val actionPanel = ActionPanel(this)
    private val mainPanel = MainPanel(deviceListPanel, romCollectionPanel, actionPanel)

    private var selectedDevice: Device? = null

    init {
        renderDevices()
        renderLocalRoms()
    }

    fun start() {
        mainPanel.isVisible = true
    }

    override fun noDeviceSelected() {
        this.selectedDevice = null
        logger.debug("no device selected")
        renderLocalRoms()
        actionPanel.noOnlineDeviceSelected()
    }

    override fun offlineDeviceSelected(device: Device) {
        this.selectedDevice = null
        logger.debug("offline device selected $device")
        clearTable()
        actionPanel.noOnlineDeviceSelected()
    }

    override fun onlineDeviceSelected(device: Device) {
        this.selectedDevice = device
        logger.debug("online device selected $device")
        renderDeviceSyncStatus(device)
        actionPanel.onlineDeviceSelected()
    }

    private fun renderDevices() {
        deviceListPanel.renderDevices(deviceManager.loadDevices())
    }

    private fun renderLocalRoms() {
        if (!romCollectionPanel.isLocalRomsRendered()) {
            val localRoms = localRomManager.listLocalRoms()
            romCollectionPanel.render(localRoms)
        }
    }

    private fun renderDeviceSyncStatus(device: Device) {
        val syncStatus = localRomManager.calculateDeviceSyncStatus(device)
        romCollectionPanel.render(syncStatus)
    }

    private fun clearTable() {
        romCollectionPanel.clearTable()
    }

    override fun downloadSavesFromDevice() {
        selectedDevice?.let { device ->
            val deviceSyncStatus = romCollectionPanel.renderedDeviceSyncStatus()
            if (deviceSyncStatus != null) {
                saveFilesManager.downloadAllSavesFromDevice(device, deviceSyncStatus)
                renderDeviceSyncStatus(device)
            }
        }
    }

    override fun uploadSavesToDevice() {
        logger.warn("TODO: uploadSavesToDevice")
    }

}
