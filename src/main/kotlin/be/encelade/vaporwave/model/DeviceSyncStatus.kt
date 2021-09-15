package be.encelade.vaporwave.model

import be.encelade.vaporwave.model.roms.*
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*

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

    fun romSyncStatusOf(romId: RomId): RomSyncStatus {
        return romSyncDiff.findStatusBy(romId)
    }

    fun saveSyncStatusOf(romId: RomId): SaveSyncStatus {
        return saveSyncMap[romId] ?: SAVE_STATUS_UNKNOWN
    }

    fun listSaveFilesToDownloadFromDevice(): List<RemoteRom> {
        val statusValues = listOf(SAVE_ONLY_ON_DEVICE, SAVE_MORE_RECENT_ON_DEVICE)
        return saveSyncMap
                .filter { (_, saveSyncState) -> statusValues.contains(saveSyncState) }
                .flatMap { (romId, _) -> remoteRoms.filter { rom -> rom.matchesBy(romId) } }
    }

}
