package be.encelade.vaporwave.services

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.Rom.Companion.areEquals
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.model.roms.RomSyncDiff
import be.encelade.vaporwave.model.roms.RomSyncStatus.*
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*
import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import be.encelade.vaporwave.services.ExtensionMap.getRomExtensionsPerConsole
import be.encelade.vaporwave.services.ExtensionMap.saveFilesExtension
import be.encelade.vaporwave.services.SaveComparator.compareSaveFiles
import be.encelade.vaporwave.utils.CollectionUtils.exists
import be.encelade.vaporwave.utils.LazyLogging
import java.io.File
import java.io.File.separator
import java.nio.file.Files
import java.nio.file.attribute.FileTime

class LocalRomManager(localRomFolder: String) : LazyLogging {

    private val folder = File(localRomFolder)

    fun validate() {
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Folder $folder doesn't exist")
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun listLocalRoms(): List<LocalRom> {
        return folder
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
                                val saveFiles = files.filter { file -> saveFilesExtension.contains(file.extension) }
                                LocalRom(consoleFolder.name, simpleFileName, romFiles, saveFiles)
                            }
                }
                .map { localRom -> localRom.attachCompanionFiles() }
                .sortedWith(ConsoleAndNameRomComparator)
    }

    fun calculateDeviceSyncStatus(device: Device): DeviceSyncStatus? {
        val client = DeviceClient.forDevice(device)

        return if (client != null) {
            val localRoms = listLocalRoms()
            val remoteRoms = client.listRoms()
            val romSyncDiff = calculateSyncDiff(localRoms, remoteRoms)
            val saveSyncMap = calculateSaveSyncStatusMap(localRoms, remoteRoms, romSyncDiff)
            DeviceSyncStatus(localRoms, remoteRoms, romSyncDiff, saveSyncMap)
        } else {
            null
        }
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
                .map { rom -> rom.romId() }
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

    fun downloadSavesFromDevice(device: Device, deviceSyncStatus: DeviceSyncStatus) {
        val allEntries = deviceSyncStatus.saveToDownloadFromDevices().flatMap { remoteRom -> remoteRom.saveFiles }
        allEntries.forEach { entry -> logger.debug("to download: ${entry.filePath}") }

        DeviceClient.forDevice(device)?.let { client ->
            allEntries
                    .groupBy { entry -> entry.console() }
                    .toList()
                    .sortedBy { (console, _) -> console }
                    .forEach { (console, consoleEntries) ->
                        val filePaths = consoleEntries.map { entry -> entry.filePath }
                        val targetFolder = "${folder.absolutePath}$separator${console}$separator"
                        val files = client.downloadFiles(filePaths, targetFolder)
                        if (files.size == consoleEntries.size) {
                            files.indices.forEach { i ->
                                val fileTime = FileTime.fromMillis(consoleEntries[i].lastModified.millis)
                                val path = files[i].toPath()
                                Files.setLastModifiedTime(path, fileTime)
                            }
                        } else {
                            logger.error("inconsistent number of files!")
                            files.forEach { file -> file.delete() }
                        }
                    }
        }
    }

}
