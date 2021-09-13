package be.encelade.vaporwave.gui

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.LocalRomManager
import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import javax.swing.JFrame

class MainGui(private val deviceManager: DeviceManager,
              private val localRomManager: LocalRomManager) :
        JFrame(), DeviceSelectionGuiCallback, LazyLogging {

    private val deviceListPanel = DeviceListPanel(this)
    private val romCollectionPanel = RomCollectionPanel()
    private val actionPanel = ActionPanel()

    private var isShowingOnlyLocal = false

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
        logger.debug("no device selected")
        renderLocalRoms()
        actionPanel.noOnlineDeviceSelected()
    }

    override fun offlineDeviceSelected(device: Device) {
        logger.debug("offline device selected $device")
        clearRomsTable()
        actionPanel.noOnlineDeviceSelected()
    }

    override fun onlineDeviceSelected(device: Device) {
        logger.debug("online device selected $device")
        renderAllRoms(device)
        actionPanel.onlineDeviceSelected()
    }

    private fun renderDevices() {
        deviceListPanel.renderDevices(deviceManager.loadDevices())
    }

    private fun clearRomsTable() {
        romCollectionPanel.clearRomsTable()
        isShowingOnlyLocal = false
    }

    private fun renderLocalRoms() {
        if (!isShowingOnlyLocal) {
            val localRoms = localRomManager.listLocalRoms()
            romCollectionPanel.renderLocalRoms(localRoms)
            isShowingOnlyLocal = true
        }
    }

    private fun renderAllRoms(device: Device) {
        DeviceClient.forDevice(device)?.let { client ->
            val localRoms = localRomManager.listLocalRoms()
            val remoteRoms = client.listRoms()
            val syncDiff = localRomManager.calculateSyncDiff(localRoms, remoteRoms)
            romCollectionPanel.renderAllRoms(localRoms, remoteRoms, syncDiff)
            isShowingOnlyLocal = false
        }
    }

}
