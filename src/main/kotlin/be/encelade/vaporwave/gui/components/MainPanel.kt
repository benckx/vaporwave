package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import javax.swing.JFrame

class MainPanel(deviceListPanel: DeviceListPanel,
                romCollectionPanel: RomCollectionPanel,
                actionPanel: ActionPanel) : JFrame(), LazyLogging {

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
    }

}
