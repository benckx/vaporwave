package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LocalRom
import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.model.RomSyncStatus
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
                            .filter { file -> getExtensionPerConsole(consoleFolder.name).contains(file.extension) }
                            .groupBy { file -> file.nameWithoutExtension }
                            .map { (simpleFileName, files) -> LocalRom(consoleFolder.name, simpleFileName, files) }
                }
                .map { localRom -> localRom.attachCompanionFiles() }
    }

    fun calculateSyncStatus(remoteRoms: List<RemoteRom>, localRoms: List<LocalRom> = listLocalRoms()): RomSyncStatus {
        val synced = localRoms.filter { localRom -> remoteRoms.find { remoteRom -> areEquals(localRom, remoteRom) } != null }
        val notOnDevice = localRoms.filterNot { localRom -> remoteRoms.find { remoteRom -> areEquals(localRom, remoteRom) } != null }
        val notOnLocal = remoteRoms.filterNot { remoteRom -> localRoms.find { localRom -> areEquals(localRom, remoteRom) } != null }
        return RomSyncStatus(synced, notOnDevice, notOnLocal)
    }

    companion object {

        fun areEquals(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
            return localRom.console == remoteRom.console && localRom.simpleFileName == remoteRom.simpleFileName
        }

    }

}
