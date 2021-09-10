package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LocalRom
import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import be.encelade.vaporwave.services.ExtensionMap.getExtensionPerConsole
import java.io.File

class LocalRomManager(localRomFolder: String) {

    private val folder = File(localRomFolder)

    init {
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Folder $localRomFolder doesn't exist")
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun listLocalRoms(): List<LocalRom> {
        return folder
                .listFiles()
                .filter { it.isDirectory && consoleKeys.contains(it.name) }
                .flatMap { consoleFolder ->
                    consoleFolder
                            .listFiles()
                            .filter { getExtensionPerConsole(consoleFolder.name).contains(it.extension) }
                            .groupBy { it.nameWithoutExtension }
                            .map { (simpleFileName, files) -> LocalRom(consoleFolder.name, simpleFileName, files) }
                }
                .map { localRom -> localRom.attachCompanionFile() }
    }

}
