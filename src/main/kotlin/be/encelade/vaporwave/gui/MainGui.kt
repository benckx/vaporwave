package be.encelade.vaporwave.gui

import be.encelade.vaporwave.model.DeviceSyncStatus
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
    private var renderedLocalRoms = false
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null // FIXME: redundant with variable in RomCollectionPanel?

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

        renderLocalRoms()
        renderDevices()
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
        clearRomsTable()
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
        if (!renderedLocalRoms) {
            val localRoms = localRomManager.listLocalRoms()
            romCollectionPanel.render(localRoms)
            renderedLocalRoms = true
            renderedDeviceSyncStatus = null
        }
    }

    private fun renderDeviceSyncStatus(device: Device) {
        localRomManager
                .calculateDeviceSyncStatus(device)
                ?.let { syncStatus ->
                    romCollectionPanel.render(syncStatus)
                    renderedLocalRoms = false
                    renderedDeviceSyncStatus = syncStatus
                }
    }

    private fun clearRomsTable() {
        romCollectionPanel.clearRomsTable()
        renderedLocalRoms = false
    }

    override fun downloadSavesFromDevice() {
        if (selectedDevice != null && renderedDeviceSyncStatus != null) {
            saveFilesManager.downloadAllSavesFromDevice(selectedDevice!!, renderedDeviceSyncStatus!!)
            renderDeviceSyncStatus(selectedDevice!!)
        }
    }

    override fun uploadSavesToDevice() {
        TODO("Not yet implemented")
    }

}
