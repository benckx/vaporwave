package be.encelade.vaporwave.services

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.utils.LazyLogging
import java.io.File
import java.io.File.separator
import java.nio.file.Files
import java.nio.file.attribute.FileTime

class SaveFilesManager(private val localRomFolder: File) : LazyLogging {

    fun downloadAllSavesFromDevice(device: Device, deviceSyncStatus: DeviceSyncStatus) {
        val client = DeviceClient.forDevice(device)

        deviceSyncStatus
                .listSaveFilesToDownloadFromDevice()
                .flatMap { remoteRom -> remoteRom.saveFiles }
                .groupBy { entry -> entry.console() }
                .toList()
                .sortedBy { (console, _) -> console }
                .forEach { (console, consoleEntries) ->
                    consoleEntries.forEach { entry -> logger.debug("to download: ${entry.filePath}") }
                    val filePaths = consoleEntries.map { entry -> entry.filePath }
                    val targetFolder = "${localRomFolder.absolutePath}$separator${console}$separator"
                    val files = client.downloadFilesFromDevice(filePaths, targetFolder)
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
