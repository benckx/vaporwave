package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.devices.MockDevice
import be.encelade.vaporwave.model.devices.SshDevice
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.CommandResultParser.lsEntriesToRemoteRoms
import be.encelade.vaporwave.services.CommandResultParser.parseLsResult
import be.encelade.vaporwave.services.CommandResultParser.parseMd5Result
import be.encelade.vaporwave.utils.LazyLogging
import java.io.File

abstract class DeviceClient<D : Device>(val device: D) : LazyLogging {

    abstract fun romConsoleFolder(console: String): String

    abstract fun isReachable(): Boolean

    /**
     * send 'ls' command to the device
     */
    abstract fun lsCommandRomFolder(): String

    /**
     * send 'md5sum' to the device
     */
    abstract fun md5sumCommandRomFolder(): String

    abstract fun downloadFilesFromDevice(filePairs: List<Pair<String, File>>): List<File>

    abstract fun uploadFilesToDevice(filePairs: List<Pair<File, String>>)

    fun listRoms(): List<RemoteRom> {
        val lsCommandResult = lsCommandRomFolder()
        val md5CommandResult = md5sumCommandRomFolder()
        logger.debug("ls command result:\n$lsCommandResult")
        logger.debug("md5 command result:\n$md5CommandResult")
        val lsEntries = parseLsResult(lsCommandResult)
        val md5Map = parseMd5Result(md5CommandResult)
        return lsEntriesToRemoteRoms(lsEntries, md5Map)
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
