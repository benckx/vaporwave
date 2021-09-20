package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*
import org.joda.time.DateTime
import kotlin.math.absoluteValue

object SaveComparator {

    private const val DELTA_THRESHOLD = 100L

    fun compareSaveFiles(localRom: LocalRom, remoteRom: RemoteRom): SaveSyncStatus {
        return if (localRom.saveFiles.isNotEmpty() && remoteRom.saveFiles.isNotEmpty()) {
            if (areSynced(localRom, remoteRom)) {
                SAVE_SYNCED
            } else if (!sameFileNamesAndHashes(localRom, remoteRom) && isRemoteMoreRecent(localRom, remoteRom)) {
                SAVE_MORE_RECENT_ON_DEVICE
            } else if (!sameFileNamesAndHashes(localRom, remoteRom) && isLocalMoreRecent(localRom, remoteRom)) {
                SAVE_MORE_RECENT_ON_COMPUTER
            } else {
                SAVE_STATUS_UNKNOWN
            }
        } else if (localRom.saveFiles.isEmpty() && remoteRom.saveFiles.isEmpty()) {
            NO_SAVE_FOUND
        } else if (localRom.saveFiles.isNotEmpty() && remoteRom.saveFiles.isEmpty()) {
            SAVE_ONLY_ON_COMPUTER
        } else if (localRom.saveFiles.isEmpty() && remoteRom.saveFiles.isNotEmpty()) {
            SAVE_ONLY_ON_DEVICE
        } else {
            SAVE_STATUS_UNKNOWN
        }
    }

    private fun areSynced(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return sameFileNamesAndHashes(localRom, remoteRom) &&
                (areDateTimesIdentical(localRom, remoteRom) || isRemoteMoreRecent(localRom, remoteRom))
    }

    private fun areDateTimesIdentical(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return compareDateTimes(localRom, remoteRom) { localLastModified: Long, remoteLastModified: DateTime ->
            deltaInMillis(localLastModified, remoteLastModified) < DELTA_THRESHOLD
        }
    }

    private fun isRemoteMoreRecent(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return compareDateTimes(localRom, remoteRom) { localLastModified: Long, remoteLastModified: DateTime ->
            DateTime(localLastModified).isBefore(remoteLastModified)
        }
    }

    private fun isLocalMoreRecent(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return compareDateTimes(localRom, remoteRom) { localLastModified: Long, remoteLastModified: DateTime ->
            DateTime(localLastModified).isAfter(remoteLastModified)
        }
    }

    private fun compareDateTimes(localRom: LocalRom, remoteRom: RemoteRom, comparisonPredicate: (Long, DateTime) -> Boolean): Boolean {
        return if (sameFileNames(localRom, remoteRom)) {
            localRom
                    .saveFilesWithoutHash()
                    .map { file -> file.name }
                    .all { fileName ->
                        val local = localRom.saveFilesWithoutHash().find { it.name == fileName }!!
                        val remote = remoteRom.saveFilesWithoutHash().find { it.fileName() == fileName }!!
                        comparisonPredicate(local.lastModified(), remote.lastModified)
                    }
        } else {
            false
        }
    }

    private fun sameFileNames(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return localRom.saveFilesWithoutHash().map { file -> file.name }.sorted() ==
                remoteRom.saveFilesWithoutHash().map { entry -> entry.fileName() }.sorted()
    }

    private fun sameFileNamesAndHashes(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        if (sameFileNames(localRom, remoteRom)) {
            return localRom
                    .saveFilesWithoutHash()
                    .map { file -> file.name }
                    .all { fileName ->
                        val localHash = localRom.saveFiles.find { (file, _) -> fileName == file.name }!!.second
                        val remoteHash = remoteRom.saveFiles.find { (lsEntry, _) -> fileName == lsEntry.fileName() }!!.second
                        localHash == remoteHash
                    }
        }

        return false
    }

    private fun deltaInMillis(millis: Long, dateTime: DateTime): Long {
        return (millis - dateTime.millis).absoluteValue
    }

}
