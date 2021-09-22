package be.encelade.vaporwave.services

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.SaveSyncStatus
import be.encelade.vaporwave.model.SaveSyncStatus.*
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.*
import be.encelade.vaporwave.model.roms.Rom.Companion.areEquals
import be.encelade.vaporwave.model.roms.RomSyncStatus.*
import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import be.encelade.vaporwave.services.ExtensionMap.getRomExtensionsPerConsole
import be.encelade.vaporwave.services.ExtensionMap.saveFilesExtension
import be.encelade.vaporwave.services.SaveComparator.compareSaveFiles
import be.encelade.vaporwave.utils.CollectionUtils.exists
import be.encelade.vaporwave.utils.FileUtils.md5Digest
import be.encelade.vaporwave.utils.LazyLogging
import java.io.File
import java.io.File.separator

class LocalRomManager(private val localRomFolder: File) : LazyLogging {

    /**
     * Create if it doesn't exist locally
     */
    fun consoleFolder(console: String): File {
        if (!consoleKeys.contains(console)) {
            throw IllegalArgumentException("Unknown console $console")
        }

        val folder = File("${localRomFolder.absolutePath}$separator$console")

        if (folder.exists()) {
            if (!folder.isDirectory) {
                folder.delete()
                folder.mkdir()
            }
        } else {
            folder.mkdir()
        }

        return folder
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun listLocalRoms(): List<LocalRom> {
        return localRomFolder
                .listFiles()
                .filter { consoleFolder -> consoleFolder.isDirectory }
                .filter { consoleFolder -> consoleKeys.contains(consoleFolder.name) }
                .flatMap { consoleFolder ->
                    val romExtensionsForConsole = getRomExtensionsPerConsole(consoleFolder.name)

                    consoleFolder
                            .listFiles()
                            .filter { file ->
                                romExtensionsForConsole.contains(file.extension) ||
                                        saveFilesExtension.contains(file.extension)
                            }
                            .groupBy { file -> file.nameWithoutExtension }
                            .map { (simpleFileName, files) ->
                                val romFiles = files.filter { file -> romExtensionsForConsole.contains(file.extension) }
                                val saveFiles = files
                                        .filter { file -> saveFilesExtension.contains(file.extension) }
                                        .map { file -> file to file.md5Digest() }

                                LocalRom(RomId(consoleFolder.name, simpleFileName), romFiles, saveFiles)
                            }
                }
                .map { localRom -> localRom.attachCompanionFiles() }
                .sortedWith(ConsoleAndNameRomComparator)
    }

    fun calculateDeviceSyncStatus(device: Device): DeviceSyncStatus {
        val localRoms = listLocalRoms()
        val client = DeviceClient.forDevice(device)
        val remoteRoms = client.listRoms()
        val romSyncDiff = calculateSyncDiff(localRoms, remoteRoms)
        val saveSyncMap = calculateSaveSyncStatusMap(localRoms, remoteRoms, romSyncDiff)
        return DeviceSyncStatus(localRoms, remoteRoms, romSyncDiff, saveSyncMap)
    }

    fun calculateSyncDiff(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>): RomSyncDiff {
        val synced = localRoms.filter { localRom -> remoteRoms.exists { remoteRom -> areEquals(localRom, remoteRom) } }
        val notOnDevice = localRoms.filterNot { localRom -> remoteRoms.exists { remoteRom -> areEquals(localRom, remoteRom) } }
        val notOnLocal = remoteRoms.filterNot { remoteRom -> localRoms.exists { localRom -> areEquals(localRom, remoteRom) } }

        return RomSyncDiff(synced, notOnDevice, notOnLocal)
                .reCalculateForCueFiles()
                .sortedWith(ConsoleAndNameRomComparator)
    }

    private fun calculateSaveSyncStatusMap(localRoms: List<LocalRom>,
                                           remoteRoms: List<RemoteRom>,
                                           romSyncDiff: RomSyncDiff): Map<RomId, SaveSyncStatus> {
        val result = mutableMapOf<RomId, SaveSyncStatus>()

        (localRoms + remoteRoms)
                .sortedWith(ConsoleAndNameRomComparator)
                .map { rom -> rom.romId }
                .distinct()
                .forEach { romId ->
                    val localRom = localRoms.find { rom -> rom.matchesBy(romId) }
                    val remoteRom = remoteRoms.find { rom -> rom.matchesBy(romId) }

                    result[romId] = when (romSyncDiff.findStatusBy(romId)) {
                        ROM_SYNCED -> {
                            if (localRom != null && remoteRom != null) {
                                compareSaveFiles(localRom, remoteRom)
                            } else {
                                SAVE_STATUS_UNKNOWN
                            }
                        }
                        ROM_ONLY_ON_COMPUTER -> {
                            if (localRom != null && localRom.saveFiles.isNotEmpty()) {
                                SAVE_ONLY_ON_COMPUTER
                            } else {
                                NO_SAVE_FOUND
                            }
                        }
                        ROM_ONLY_ON_DEVICE -> {
                            if (remoteRom != null && remoteRom.saveFiles.isNotEmpty()) {
                                SAVE_ONLY_ON_DEVICE
                            } else {
                                NO_SAVE_FOUND
                            }
                        }
                        else -> SAVE_STATUS_UNKNOWN
                    }
                }

        return result
    }

}
