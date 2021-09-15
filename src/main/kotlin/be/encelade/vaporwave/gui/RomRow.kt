package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.Rom
import be.encelade.vaporwave.model.roms.RomSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * As [Rom], as shown in the rom collection panel.
 */
internal data class RomRow(val localRom: LocalRom?,
                           val remoteRom: RemoteRom?,
                           val romSyncStatus: RomSyncStatus,
                           val saveSyncStatus: SaveSyncStatus) {

    private val allRoms = listOfNotNull(localRom, remoteRom)
    private val rom: Rom<*> = allRoms.first()

    fun render(): Array<String> {
        val lastModified = lastModified()

        val row = mutableListOf<String>()
        row += romSyncStatus.lowerCase()
        row += rom.console()
        row += rom.simpleFileName()
        row += renderFileList(rom.romFiles)
        row += humanReadableByteCountBin(rom.romFilesSize())
        row += if (saveSyncStatus == NO_SAVE_FOUND) {
            NO_VALUE_CELL
        } else {
            saveSyncStatus.lowerCase()
        }
        row += if (lastModified != null) {
            dateFormat.print(lastModified)
        } else {
            NO_VALUE_CELL
        }
        return row.toTypedArray()
    }

    private fun lastModified(): LocalDateTime? {
        return when (saveSyncStatus) {
            SAVE_SYNCED,
            SAVE_ONLY_ON_COMPUTER,
            SAVE_MORE_RECENT_ON_COMPUTER -> localRom!!.saveFileLastModified()
            SAVE_ONLY_ON_DEVICE,
            SAVE_MORE_RECENT_ON_DEVICE -> remoteRom!!.saveFileLastModified()
            else -> null
        }
    }

    fun console(): String = rom.console()

    fun simpleFileName(): String = rom.simpleFileName()

    fun romFilesSize() = rom.romFilesSize()

    private companion object {

        const val NO_VALUE_CELL = "--"

        val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")

        fun renderFileList(list: List<*>): String {
            return when (list.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${list.size} files"
            }
        }

    }

}
