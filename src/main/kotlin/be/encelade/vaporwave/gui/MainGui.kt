package be.encelade.vaporwave.gui

import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.RomSyncDiff
import java.awt.BorderLayout
import javax.swing.JFrame

class MainGui : JFrame() {

    private val romCollectionPanel = RomCollectionPanel()

    init {
        title = "Vaporwave"
        setBounds(500, 200, 1400, 1200)
        layout = BorderLayout()
        add(romCollectionPanel, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        romCollectionPanel.renderLocalRoms(localRoms)
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, syncDiff: RomSyncDiff) {
        romCollectionPanel.renderAllRoms(localRoms, remoteRoms, syncDiff)
    }

}
