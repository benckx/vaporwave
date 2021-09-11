package be.encelade.vaporwave.gui

import be.encelade.vaporwave.model.LocalRom
import java.awt.BorderLayout
import javax.swing.JFrame

class MainGui : JFrame() {

    private val romCollectionPanel = RomCollectionPanel()

    init {
        title = "Vaporwave"
        setBounds(500, 200, 1000, 1200)
        layout = BorderLayout()
        add(romCollectionPanel, BorderLayout.CENTER)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun loadLocalRoms(localRoms: List<LocalRom>) {
        romCollectionPanel.loadLocalRoms(localRoms)
    }

}
