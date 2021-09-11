package be.encelade.vaporwave.model

data class RomSyncStatus(val synced: List<LocalRom>,
                         val notOnDevice: List<LocalRom>,
                         val notOnLocalFolder: List<RemoteRom>) {

    fun reCalculateForCueFiles(): RomSyncStatus {
        val synced = this.synced.toMutableList()
        val notOnDevice = this.notOnDevice.toMutableList()
        val notOnLocalFolder = this.notOnLocalFolder.toMutableList()
        val filesFromCue = synced.flatMap { localRom -> localRom.listFilesFromCue() }
        val toRemoveFromNotOnLocal = mutableListOf<RemoteRom>()

        notOnLocalFolder.forEach { remoteRom ->
            filesFromCue.forEach { fileFromCue ->
                if (remoteRom.simpleFileName == fileFromCue.nameWithoutExtension) {
                    notOnDevice.find { localRom -> localRom.listFilesFromCue().contains(fileFromCue) }?.let { localRom -> synced += localRom }
                    toRemoveFromNotOnLocal += remoteRom
                }
            }
        }

        val cleanedNotOnLocalFolder = notOnLocalFolder.filterNot { remoteRom -> toRemoveFromNotOnLocal.contains(remoteRom) }
        return RomSyncStatus(synced.distinct().sortedBy { it.simpleFileName }, notOnDevice, cleanedNotOnLocalFolder)
    }

}
