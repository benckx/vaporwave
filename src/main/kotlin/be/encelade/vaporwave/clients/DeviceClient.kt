package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.LSParser.findRemoveRoms
import be.encelade.vaporwave.services.LSParser.parseLsResult

abstract class DeviceClient<D : Device>(protected val device: D) {

    abstract fun isReachable(): Boolean

    abstract fun listRomFolderFiles(): String

    fun listRoms(): List<RemoteRom> {
        val entries = parseLsResult(listRomFolderFiles())
        return findRemoveRoms(entries)
    }

}
