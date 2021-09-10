package be.encelade.vaporwave.services

import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import java.io.File

class LocalRomManager(private val localRomFolder: String) {

    private val folder = File(localRomFolder)

    init {
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Folder $localRomFolder doesn't exist")
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun listLocalRoms() {
        folder
                .listFiles()
                .filter { it.isDirectory && consoleKeys.contains(it.name) }
                .forEach { println(it) }
    }

}
