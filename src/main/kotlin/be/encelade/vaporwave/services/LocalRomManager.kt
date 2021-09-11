package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LocalRom
import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.model.RomSyncStatus
import be.encelade.vaporwave.model.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import be.encelade.vaporwave.services.ExtensionMap.getExtensionPerConsole
import be.encelade.vaporwave.utils.CollectionUtils.exists
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
                .filter { consoleFolder -> consoleFolder.isDirectory }
                .filter { consoleFolder -> consoleKeys.contains(consoleFolder.name) }
                .flatMap { consoleFolder ->
                    consoleFolder
                            .listFiles()
                            .filter { file -> getExtensionPerConsole(consoleFolder.name).contains(file.extension) }
                            .groupBy { file -> file.nameWithoutExtension }
                            .map { (simpleFileName, files) -> LocalRom(consoleFolder.name, simpleFileName, files) }
                }
                .map { localRom -> localRom.attachCompanionFiles() }
    }

    fun calculateSyncStatus(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>): RomSyncStatus {
        val synced = localRoms.filter { localRom -> remoteRoms.exists { remoteRom -> areEquals(localRom, remoteRom) } }
        val notOnDevice = localRoms.filterNot { localRom -> remoteRoms.exists { remoteRom -> areEquals(localRom, remoteRom) } }
        val notOnLocal = remoteRoms.filterNot { remoteRom -> localRoms.exists { localRom -> areEquals(localRom, remoteRom) } }

        return RomSyncStatus(synced, notOnDevice, notOnLocal)
                .reCalculateForCueFiles()
                .sortedWith(ConsoleAndNameRomComparator)
    }

    companion object {

        fun areEquals(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
            return localRom.console == remoteRom.console &&
                    localRom.simpleFileName == remoteRom.simpleFileName
        }

    }

}
