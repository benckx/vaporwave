package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import javax.swing.JFrame

class MainWindow(deviceListPanel: DeviceListPanel,
                 romCollectionPanel: RomCollectionPanel) : JFrame(), LazyLogging {

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
        defaultCloseOperation = EXIT_ON_CLOSE
    }

}
