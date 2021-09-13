package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*
import org.joda.time.DateTime
import kotlin.math.absoluteValue

object SaveComparator {

    private const val DELTA_THRESHOLD = 10 * 1000L

    fun compareSaveFiles(localRom: LocalRom, remoteRom: RemoteRom): SaveSyncStatus {
        return if (localRom.saveFiles.isNotEmpty() && remoteRom.saveFiles.isNotEmpty()) {
            if (areSynced(localRom, remoteRom)) {
                SAVE_SYNCED
            } else if (isRemoteMoreRecent(localRom, remoteRom)) {
                SAVE_MORE_RECENT_ON_DEVICE
            } else if (isLocalMoreRecent(localRom, remoteRom)) {
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
        return if (sameFiles(localRom, remoteRom)) {
            localRom.saveFiles.map { file -> file.name }.all { fileName ->
                val local = localRom.saveFiles.find { it.name == fileName }!!
                val remote = remoteRom.saveFiles.find { it.fileName() == fileName }!!
                deltaInMillis(local.length(), remote.lastModified) < DELTA_THRESHOLD && local.length() == remote.fileSize
            }
        } else {
            false
        }
    }

    private fun isRemoteMoreRecent(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return if (sameFiles(localRom, remoteRom)) {
            localRom.saveFiles.map { file -> file.name }.all { fileName ->
                val local = localRom.saveFiles.find { it.name == fileName }!!
                val remote = remoteRom.saveFiles.find { it.fileName() == fileName }!!
                DateTime(local.lastModified()).isBefore(remote.lastModified)
            }
        } else {
            false
        }
    }

    private fun isLocalMoreRecent(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return if (sameFiles(localRom, remoteRom)) {
            localRom.saveFiles.map { file -> file.name }.all { fileName ->
                val local = localRom.saveFiles.find { it.name == fileName }!!
                val remote = remoteRom.saveFiles.find { it.fileName() == fileName }!!
                DateTime(local.lastModified()).isAfter(remote.lastModified)
            }
        } else {
            false
        }
    }

    private fun sameFiles(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
        return localRom.saveFiles.map { file -> file.name }.sorted() == remoteRom.saveFiles.map { entry -> entry.fileName() }.sorted()
    }

    private fun deltaInMillis(millis: Long, dateTime: DateTime): Long {
        return (millis - dateTime.millis).absoluteValue
    }

}
