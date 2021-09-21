package be.encelade.vaporwave

import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.services.GuiController
import be.encelade.vaporwave.services.LocalRomManager
import be.encelade.vaporwave.utils.PropertiesFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.File.separator
import javax.swing.JFileChooser
import javax.swing.JFileChooser.APPROVE_OPTION
import javax.swing.JFileChooser.DIRECTORIES_ONLY
import javax.swing.JOptionPane.WARNING_MESSAGE
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.UIManager
import kotlin.system.exitProcess

const val LOCAL_ROM_FOLDER = "LOCAL_ROM_FOLDER"
val PREFERENCES_FILE_LOCATION = "data${separator}preferences.properties"

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val logger: Logger = LoggerFactory.getLogger("Main")

    val localRomFolderPath = locateLocalRomFolder()
    if (localRomFolderPath != null) {
        val localRomFolder = File(localRomFolderPath)
        if (!localRomFolder.exists() || !localRomFolder.isDirectory) {
            throw IllegalArgumentException("Folder $localRomFolder doesn't exist")
        }

        val deviceManager = DeviceManager()
        val localRomManager = LocalRomManager(localRomFolder)
        val controller = GuiController(deviceManager, localRomManager)
        controller.start()
    } else {
        logger.error("local rom folder unknown, leaving app")
        exitProcess(0)
    }
}

private fun locateLocalRomFolder(): String? {
    var localRomFolderPath: String? = null
    val propertiesFile = PropertiesFile(PREFERENCES_FILE_LOCATION)
    if (!propertiesFile.isDefined(LOCAL_ROM_FOLDER)) {
        showMessageDialog(null, "Local Rom Folder not defined\nPlease select local rom folder", "Local Rom Folder", WARNING_MESSAGE)

        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = DIRECTORIES_ONLY
        val returnValue = fileChooser.showOpenDialog(null)
        if (returnValue == APPROVE_OPTION) {
            val folder = fileChooser.selectedFile
            if (folder.isDirectory && folder.exists()) {
                propertiesFile.persistProperty(LOCAL_ROM_FOLDER, folder.absolutePath)
                localRomFolderPath = folder.absolutePath
            }
        } else {
            localRomFolderPath = propertiesFile.getProperty(LOCAL_ROM_FOLDER)!!
        }
    }
    return localRomFolderPath
}
