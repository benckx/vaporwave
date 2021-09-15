package be.encelade.vaporwave

import be.encelade.vaporwave.gui.MainGui
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.LocalRomManager
import be.encelade.vaporwave.services.SaveFilesManager
import java.io.File
import javax.swing.UIManager

fun main() {
    val localRomFolder = File("/home/benoit/roms")
    if (!localRomFolder.exists() || !localRomFolder.isDirectory) {
        throw IllegalArgumentException("Folder $localRomFolder doesn't exist")
    }

    val deviceManager = DeviceManager()
    val localRomManager = LocalRomManager(localRomFolder)
    val saveFilesManager = SaveFilesManager(localRomFolder)

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val gui = MainGui(deviceManager, localRomManager, saveFilesManager)
    gui.isVisible = true
}
