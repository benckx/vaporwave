package be.encelade.vaporwave.services

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.gui.api.ActionPanelCallback
import be.encelade.vaporwave.gui.api.DevicePanelCallback
import be.encelade.vaporwave.gui.api.RomCollectionCallback
import be.encelade.vaporwave.gui.components.*
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.lang3.BooleanUtils.isTrue
import kotlin.concurrent.thread

class GuiController(deviceManager: DeviceManager,
                    private val localRomManager: LocalRomManager,
                    private val saveFilesManager: SaveFilesManager) :
        DevicePanelCallback, ActionPanelCallback, RomCollectionCallback, LazyLogging {

    private val rightClickMenu = RomCollectionRightClickMenu()

    private val deviceListPanel = DeviceListPanel(this)
    private val romCollectionPanel = RomCollectionPanel(rightClickMenu, this)
    private val actionPanel = ActionPanel(this)
    private val mainPanel = MainPanel(deviceListPanel, romCollectionPanel, actionPanel)

    private var devices = listOf<Device>()
    private val isOnlineMap = mutableMapOf<Device, Boolean>()
    private var selectedDevice: Device? = null
    private var renderedLocalRoms: List<LocalRom>? = null
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null

    init {
        devices = deviceManager.loadDevices()
        deviceListPanel.renderDevices(devices)

        renderLocalRoms()
        refreshStatus()
    }

    fun start() {
        mainPanel.isVisible = true
    }

    override fun deviceSelected(idx: Int) {
        fun noDeviceSelected() {
            this.selectedDevice = null
            logger.debug("no device selected")
            renderLocalRoms()
            actionPanel.disableButtons()
        }

        fun offlineDeviceSelected(device: Device) {
            this.selectedDevice = null
            logger.debug("offline device selected $device")
            romCollectionPanel.clearTable()
            actionPanel.disableButtons()
        }

        fun onlineDeviceSelected(device: Device) {
            this.selectedDevice = device
            logger.debug("online device selected $device")
            renderDeviceSyncStatus(device)
            actionPanel.enableButtons()
        }

        if (idx >= 0) {
            this.selectedDevice = devices[idx]
            selectedDevice?.let { device ->
                if (isTrue(isOnlineMap[device])) {
                    onlineDeviceSelected(device)
                } else {
                    offlineDeviceSelected(device)
                }
            }
        } else {
            noDeviceSelected()
        }
    }

    override fun refreshButtonClicked() {
        refreshStatus()
    }

    override fun unSelectButtonClicked() {
        this.selectedDevice = null
        romCollectionPanel.clearTable()
    }

    private fun refreshStatus() {
        val clients = devices.map { device -> DeviceClient.forDevice(device) }

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            deviceListPanel.setConnectingStatus(i)
        }

        clients.forEach { client ->
            thread {
                val i = devices.indexOf(client.device)
                val isOnlineBefore = isOnlineMap[client.device]
                val isOnlineNow = client.isReachable()
                isOnlineMap[client.device] = isOnlineNow
                deviceListPanel.setOnlineStatus(isOnlineNow, i)

                if (isOnlineBefore != isOnlineNow) {
                    // TODO: update rom list if device came online and is selected
                }
            }
        }
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
        val selectedRomIds = romCollectionPanel.listSelectedRomIds()
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
