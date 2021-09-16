package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.devices.MockDevice
import be.encelade.vaporwave.model.devices.SshDevice
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.LSParser.findRemoteRoms
import be.encelade.vaporwave.services.LSParser.parseLsResult
import be.encelade.vaporwave.utils.LazyLogging
import java.io.File

abstract class DeviceClient<D : Device>(val device: D) : LazyLogging {

    abstract fun isReachable(): Boolean

    /**
     * send 'ls' command to the device
     */
    abstract fun listRomFolderFiles(): String

    abstract fun downloadFilesFromDevice(filePaths: List<String>, targetFolder: String): List<File>

    abstract fun uploadFilesToDevice(files: List<Pair<File, String>>)

    fun listRoms(): List<RemoteRom> {
        val result = listRomFolderFiles()
        logger.debug("command result:\n$result")
        val entries = parseLsResult(result)
        return findRemoteRoms(entries)
    }

    companion object : LazyLogging {

        fun forDevice(device: Device): DeviceClient<*> {
            return when (device) {
                is MockDevice -> MockDeviceClient(device)
                is SshDevice -> SshDeviceClient(device)
                else -> {
                    throw NotImplementedError("can not create client for $device")
                }
            }
        }

    }

}
