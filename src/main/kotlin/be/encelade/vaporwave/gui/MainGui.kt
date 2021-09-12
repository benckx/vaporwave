package be.encelade.vaporwave.gui

import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.RomSyncDiff
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import javax.swing.JFrame

class MainGui : JFrame() {

    private val deviceListPanel = DeviceListPanel()
    private val romCollectionPanel = RomCollectionPanel()

    init {
        val x = 500
        val y = 200
        val width = 1300
        val height = 1200

        title = "Vaporwave"
        setBounds(x, y, width, height)
        layout = BorderLayout()
        add(deviceListPanel, NORTH)
        add(romCollectionPanel, CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun renderDevices(devices: List<Device>) {
        deviceListPanel.renderDevices(devices)
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        romCollectionPanel.renderLocalRoms(localRoms)
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, syncDiff: RomSyncDiff) {
        romCollectionPanel.renderAllRoms(localRoms, remoteRoms, syncDiff)
    }

}
