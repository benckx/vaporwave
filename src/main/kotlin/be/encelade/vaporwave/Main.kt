package be.encelade.vaporwave

import be.encelade.vaporwave.gui.MainGui
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.LocalRomManager
import javax.swing.UIManager

fun main() {
    val deviceManager = DeviceManager()
    val localRomManager = LocalRomManager("/home/benoit/roms")

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val gui = MainGui(deviceManager, localRomManager)
    gui.isVisible = true
}
