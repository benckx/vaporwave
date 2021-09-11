package be.encelade.vaporwave.gui

import be.encelade.vaporwave.model.LocalRom
import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.model.RomSyncStatus
import java.awt.BorderLayout
import javax.swing.JFrame

class MainGui : JFrame() {

    private val romCollectionPanel = RomCollectionPanel()

    init {
        title = "Vaporwave"
        setBounds(500, 200, 1600, 1200)
        layout = BorderLayout()
        add(romCollectionPanel, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        romCollectionPanel.renderLocalRoms(localRoms)
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, syncStatus: RomSyncStatus) {
        romCollectionPanel.renderAllRoms(localRoms, remoteRoms, syncStatus)
    }

}
