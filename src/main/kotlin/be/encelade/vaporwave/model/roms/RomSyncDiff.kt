package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.model.roms.RomSyncStatus.*
import be.encelade.vaporwave.utils.CollectionUtils.exists

data class RomSyncDiff(val synced: List<LocalRom>,
                       val notOnDevice: List<LocalRom>,
                       val notInLocalFolder: List<RemoteRom>) {

    /**
     * For the [RemoteRom], we don't have the cue files content,
     * so we use the local ones when possible.
     */
    fun reCalculateForCueFiles(): RomSyncDiff {
        val synced = this.synced.toMutableList()
        val notOnDevice = this.notOnDevice.toMutableList()
        val notInLocalFolder = this.notInLocalFolder.toMutableList()
        val filesFromCue = synced.flatMap { localRom -> localRom.listFilesFromCue() }
        val toRemoveFromNotOnLocal = mutableListOf<RemoteRom>()

        notInLocalFolder.forEach { remoteRom ->
            filesFromCue.forEach { fileFromCue ->
                if (remoteRom.simpleFileName == fileFromCue.nameWithoutExtension) {
                    // remote rom is actually synced, so we add it to "synced roms" and remove it from "not in local folder"
                    notOnDevice
                            .find { localRom -> localRom.listFilesFromCue().contains(fileFromCue) }
                            ?.let { localRom -> synced += localRom }

                    toRemoveFromNotOnLocal += remoteRom
                }
            }
        }

        val cleanedNotOnLocalFolder = notInLocalFolder.filterNot { remoteRom -> toRemoveFromNotOnLocal.contains(remoteRom) }
        return RomSyncDiff(synced.distinct(), notOnDevice, cleanedNotOnLocalFolder)
    }

    fun sortedWith(comparator: Comparator<Rom<*>>): RomSyncDiff {
        return RomSyncDiff(
                synced.sortedWith(comparator),
                notOnDevice.sortedWith(comparator),
                notInLocalFolder.sortedWith(comparator))
    }

     fun findStatusBy(romId: RomId): RomSyncStatus {
        return when {
            isSync(romId) -> ROM_SYNCED
            isOnlyOnLocal(romId) -> ROM_ONLY_ON_COMPUTER
            isOnlyOnDevice(romId) -> ROM_ONLY_ON_DEVICE
            else -> ROM_STATUS_UNKNOWN
        }
    }

    private fun isSync(romId: RomId): Boolean {
        return synced.exists { localRom -> localRom.matchesBy(romId) }
    }

    private fun isOnlyOnDevice(romId: RomId): Boolean {
        return notInLocalFolder.exists { localRom -> localRom.matchesBy(romId) }
    }

    private fun isOnlyOnLocal(romId: RomId): Boolean {
        return notOnDevice.exists { localRom -> localRom.matchesBy(romId) }
    }

}
