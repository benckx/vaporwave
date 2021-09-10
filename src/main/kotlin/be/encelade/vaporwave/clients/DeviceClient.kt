package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.services.LSParser.findRemoveRoms
import be.encelade.vaporwave.services.LSParser.parseLsResult

interface DeviceClient {

    fun isReachable(): Boolean

    fun listRomFolderFiles(): String

    fun listRoms(): List<RemoteRom> {
        val entries = parseLsResult(listRomFolderFiles())
        return findRemoveRoms(entries)
    }

}
