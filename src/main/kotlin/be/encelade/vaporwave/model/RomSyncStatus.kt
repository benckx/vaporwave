package be.encelade.vaporwave.model

data class RomSyncStatus(val synced: List<LocalRom>,
                         val notOnDevice: List<LocalRom>,
                         val notInLocalFolder: List<RemoteRom>) {

    fun reCalculateForCueFiles(): RomSyncStatus {
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
        return RomSyncStatus(synced.distinct(), notOnDevice, cleanedNotOnLocalFolder)
    }

    fun sortedWith(comparator: Comparator<Rom<*>>): RomSyncStatus {
        return RomSyncStatus(
                synced.sortedWith(comparator),
                notOnDevice.sortedWith(comparator),
                notInLocalFolder.sortedWith(comparator))
    }

}
