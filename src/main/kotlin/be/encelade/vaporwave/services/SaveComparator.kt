package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*

object SaveComparator {

    fun calculateSyncStatus(localRom: LocalRom, remoteRom: RemoteRom): SaveSyncStatus {
        return if (localRom.saveFiles.isNotEmpty() && remoteRom.saveFiles.isNotEmpty()) {
            // TODO: saves exists on both -> compare
            SAVE_STATUS_UNKNOWN
        } else if (localRom.saveFiles.isEmpty() && remoteRom.saveFiles.isEmpty()) {
            NO_SAVE_FOUND
        } else if (localRom.saveFiles.isNotEmpty() && remoteRom.saveFiles.isEmpty()) {
            SAVE_ONLY_ON_LOCAL
        } else if (localRom.saveFiles.isEmpty() && remoteRom.saveFiles.isNotEmpty()) {
            SAVE_ONLY_ON_DEVICE
        } else {
            SAVE_STATUS_UNKNOWN
        }
    }

}
