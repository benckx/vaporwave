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

    // gui components
    private val deviceListPanel = DeviceListPanel(this)
    private val rightClickMenu = RomCollectionRightClickMenu()
    private val romCollectionPanel = RomCollectionPanel(rightClickMenu, this)
    private val actionPanel = ActionPanel(this)
    private val mainPanel = MainPanel(deviceListPanel, romCollectionPanel, actionPanel)

    // shown states
    private var devices = listOf<Device>()
    private val isOnlineMap = mutableMapOf<Device, Boolean>()
    private var selectedDevice: Device? = null
    private var renderedLocalRoms: List<LocalRom>? = null
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null

    init {
        devices = deviceManager.loadDevices()
        deviceListPanel.renderDevices(devices)

        renderLocalRoms()
        refreshOnlineStatus()
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
            renderDeviceSyncStatus()
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

    override fun refreshDevicesButtonClicked() {
        refreshOnlineStatus()
    }

    override fun unSelectDeviceButtonClicked() {
        this.selectedDevice = null
        romCollectionPanel.clearTable()
    }

    private fun refreshOnlineStatus() {
        val clients = devices.map { device -> DeviceClient.forDevice(device) }

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            deviceListPanel.setConnectingStatus(i)
        }

        clients.forEach { client ->
            thread {
                val i = devices.indexOf(client.device)
                val isDeviceOnlineBefore = isOnlineMap[client.device]
                val isDeviceOnlineNow = client.isReachable()
                isOnlineMap[client.device] = isDeviceOnlineNow
                deviceListPanel.setOnlineStatus(isDeviceOnlineNow, i)

                if (isDeviceOnlineBefore != isDeviceOnlineNow) {
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

    private fun renderDeviceSyncStatus() {
        selectedDevice?.let { device ->
            val deviceSyncStatus = localRomManager.calculateDeviceSyncStatus(device)
            romCollectionPanel.render(deviceSyncStatus)
            this.renderedLocalRoms = null
            this.renderedDeviceSyncStatus = deviceSyncStatus
        }
    }

    override fun romTableHeaderColumnClicked() {
        renderedLocalRoms?.let { romCollectionPanel.render(it) }
        renderedDeviceSyncStatus?.let { romCollectionPanel.render(it) }
    }

    override fun romRableSelectionChanged() {
        val selectedRomIds = romCollectionPanel.listSelectedRomIds()
        rightClickMenu.updateEnabledItems(selectedRomIds, renderedDeviceSyncStatus)
    }

    override fun downloadSavesFromDeviceButtonClicked() {
        if (selectedDevice != null && renderedDeviceSyncStatus != null) {
            saveFilesManager.downloadAllSavesFromDevice(selectedDevice!!, renderedDeviceSyncStatus!!)
            renderDeviceSyncStatus()
        }
    }

    override fun uploadSavesToDeviceButtonClick() {
        logger.warn("TODO: uploadSavesToDevice")
    }

}
