package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.api.ActionButtonCallback
import be.encelade.vaporwave.gui.api.DeviceSelectionGuiCallback
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.LocalRomManager
import be.encelade.vaporwave.services.SaveFilesManager
import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import javax.swing.JFrame

class MainGui(private val deviceManager: DeviceManager,
              private val localRomManager: LocalRomManager,
              private val saveFilesManager: SaveFilesManager) :
        JFrame(), DeviceSelectionGuiCallback, ActionButtonCallback, LazyLogging {

    private val deviceListPanel = DeviceListPanel(this)
    private val romCollectionPanel = RomCollectionPanel()
    private val actionPanel = ActionPanel(this)

    private var selectedDevice: Device? = null

    init {
        val x = 200
        val y = 200
        val width = 1700
        val height = 1000

        title = "Vaporwave"
        setBounds(x, y, width, height)
        layout = BorderLayout()
        add(deviceListPanel, NORTH)
        add(romCollectionPanel, CENTER)
        add(actionPanel, SOUTH)
        defaultCloseOperation = EXIT_ON_CLOSE

        renderDevices()
        renderLocalRoms()
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
