package be.encelade.vaporwave.model

import be.encelade.vaporwave.model.roms.*
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.SAVE_STATUS_UNKNOWN

data class DeviceSyncStatus(private val localRoms: List<LocalRom>,
                            private val remoteRoms: List<RemoteRom>,
                            private val romSyncDiff: RomSyncDiff,
                            private val saveSyncMap: Map<RomId, SaveSyncStatus>) {

    fun romIds(): List<RomId> {
        return (localRoms + remoteRoms)
                .sortedWith(ConsoleAndNameRomComparator)
                .map { rom -> rom.romId() }
                .distinct()
    }

    fun findLocalRom(romId: RomId): LocalRom? {
        return localRoms.find { rom -> rom.matchesBy(romId) }
    }

    fun findRemoteRom(romId: RomId): RemoteRom? {
        return remoteRoms.find { rom -> rom.matchesBy(romId) }
    }

    fun romSyncStatusOf(romId: RomId): RomSyncStatus {
        return romSyncDiff.findStatusBy(romId)
    }

    fun saveSyncStatusOf(romId: RomId): SaveSyncStatus {
        return saveSyncMap[romId] ?: SAVE_STATUS_UNKNOWN
    }

}
