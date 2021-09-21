package be.encelade.vaporwave.model

import be.encelade.vaporwave.model.SaveSyncStatus.SAVE_STATUS_UNKNOWN
import be.encelade.vaporwave.model.roms.*

/**
 * Contains all data that describes the state of the sync between local ROMs collection and a given device.
 */
data class DeviceSyncStatus(private val localRoms: List<LocalRom>,
                            private val remoteRoms: List<RemoteRom>,
                            private val romSyncDiff: RomSyncDiff,
                            private val saveSyncMap: Map<RomId, SaveSyncStatus>) {

    fun allRomIds(): List<RomId> {
        return (localRoms + remoteRoms)
                .sortedWith(ConsoleAndNameRomComparator)
                .map { rom -> rom.romId }
                .distinct()
    }

    fun findLocalRom(romId: RomId): LocalRom? {
        return localRoms.find { rom -> rom.matchesBy(romId) }
    }

    fun findRemoteRom(romId: RomId): RemoteRom? {
        return remoteRoms.find { rom -> rom.matchesBy(romId) }
    }

    fun romSyncStatus(romId: RomId): RomSyncStatus {
        return romSyncDiff.findStatusBy(romId)
    }

    fun saveSyncStatus(romId: RomId): SaveSyncStatus {
        return saveSyncMap[romId] ?: SAVE_STATUS_UNKNOWN
    }

}
