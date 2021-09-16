package be.encelade.vaporwave

import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.GuiController
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
    val controller = GuiController(deviceManager, localRomManager, saveFilesManager)

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    controller.start()
}
