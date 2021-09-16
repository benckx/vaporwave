package be.encelade.vaporwave.services

import be.encelade.vaporwave.gui.api.ActionButtonCallback
import be.encelade.vaporwave.gui.api.DeviceSelectionCallback
import be.encelade.vaporwave.gui.api.TableEventCallback
import be.encelade.vaporwave.gui.components.*
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.utils.LazyLogging

class GuiController(private val deviceManager: DeviceManager,
                    private val localRomManager: LocalRomManager,
                    private val saveFilesManager: SaveFilesManager) :
        DeviceSelectionCallback, ActionButtonCallback, TableEventCallback, LazyLogging {

    private val rightClickMenu = RomCollectionRightClickMenu()

    private val deviceListPanel = DeviceListPanel(this)
    private val romCollectionPanel = RomCollectionPanel(rightClickMenu, this)
    private val actionPanel = ActionPanel(this)
    private val mainPanel = MainPanel(deviceListPanel, romCollectionPanel, actionPanel)

    private var selectedDevice: Device? = null
    private var renderedLocalRoms: List<LocalRom>? = null
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null

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
        actionPanel.disableButtons()
    }

    override fun offlineDeviceSelected(device: Device) {
        this.selectedDevice = null
        logger.debug("offline device selected $device")
        romCollectionPanel.clearTable()
        actionPanel.disableButtons()
    }

    override fun onlineDeviceSelected(device: Device) {
        this.selectedDevice = device
        logger.debug("online device selected $device")
        renderDeviceSyncStatus(device)
        actionPanel.enableButtons()
    }

    private fun renderDevices() {
        deviceListPanel.renderDevices(deviceManager.loadDevices())
    }

    private fun renderLocalRoms() {
        val localRoms = localRomManager.listLocalRoms()
        romCollectionPanel.render(localRoms)
        this.renderedLocalRoms = localRoms
        this.renderedDeviceSyncStatus = null
    }

    private fun renderDeviceSyncStatus(device: Device) {
        val deviceSyncStatus = localRomManager.calculateDeviceSyncStatus(device)
        romCollectionPanel.render(deviceSyncStatus)
        this.renderedLocalRoms = null
        this.renderedDeviceSyncStatus = deviceSyncStatus
    }

    override fun headerColumnClicked() {
        renderedLocalRoms?.let { romCollectionPanel.render(it) }
        renderedDeviceSyncStatus?.let { romCollectionPanel.render(it) }
    }

    override fun tableSelectionChanged() {
        val selectedRomIds = romCollectionPanel.listSelectedRoms()
        rightClickMenu.updateEnabledItems(selectedRomIds, renderedDeviceSyncStatus)
    }

    override fun downloadSavesFromDevice() {
        if (selectedDevice != null && renderedDeviceSyncStatus != null) {
            saveFilesManager.downloadAllSavesFromDevice(selectedDevice!!, renderedDeviceSyncStatus!!)
            renderDeviceSyncStatus(selectedDevice!!)
        }
    }

    override fun uploadSavesToDevice() {
        logger.warn("TODO: uploadSavesToDevice")
    }

}
