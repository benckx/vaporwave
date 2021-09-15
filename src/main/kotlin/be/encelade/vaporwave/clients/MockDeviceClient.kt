package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.MockDevice
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.io.FileUtils.readFileToString
import org.apache.commons.io.FileUtils.writeStringToFile
import org.apache.commons.lang3.RandomUtils.nextInt
import java.io.File
import java.io.File.separator
import kotlin.text.Charsets.UTF_8

class MockDeviceClient(device: MockDevice) : DeviceClient<MockDevice>(device), LazyLogging {

    override fun isReachable(): Boolean {
        Thread.sleep(nextInt(1, 6) * 500L)
        return true
    }

    override fun listRomFolderFiles(): String {
        return readFileToString(File("data$separator${device.mockDataFileName}"), UTF_8)
    }

    override fun downloadFilesFromDevice(filePaths: List<String>, targetFolder: String): List<File> {
        val roms = listRoms()

        val result = mutableListOf<File>()
        filePaths.forEach { filePath ->
            // locate the LsEntry
            val fileName = filePath.split("/").last()
            val console = targetFolder.split("/").filterNot { it.isEmpty() }.last()
            val simpleFileName = fileName.substring(0, fileName.lastIndexOf('.'))
            logger.debug("remote file path: $filePath")
            logger.debug("target folder: $targetFolder")
            logger.debug("looking up for: <$console, $simpleFileName>")
            val remoteRom = roms.find { it.matchesBy(RomId(console, simpleFileName)) }!!
            val saveFileEntry = remoteRom.saveFiles.find { it.fileName() == fileName }!!

            // write a text file of the same size as the remote save file
            Thread.sleep(200L)
            val mockText = (0 until saveFileEntry.fileSize).map { "x" }.joinToString("")
            val targetFile = File("$targetFolder$separator/$fileName")
            writeStringToFile(targetFile, mockText, UTF_8)
            logger.debug("created mock file $targetFile")
            result += targetFile
        }

        return result
    }

}
