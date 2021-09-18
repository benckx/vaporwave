package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.utils.TimeUtils.toLocalDateTime
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

abstract class Rom<T>(val romId: RomId,
                      val romFiles: List<T>,
                      val saveFiles: List<T>) {

    abstract fun lastModified(entry: T): DateTime

    abstract fun romFilesSize(): Long

    fun console() = romId.console

    fun simpleFileName() = romId.simpleFileName

    fun matchesBy(romId: RomId): Boolean {
        return this.romId == romId
    }

    fun matchesBy(console: String, simpleFileName: String): Boolean {
        return this.romId == RomId(console, simpleFileName)
    }

    fun allFiles(): List<T> {
        return romFiles + saveFiles
    }

    fun saveFileLastModified(): LocalDateTime? {
        return saveFiles
                .map { saveFileEntry -> toLocalDateTime(lastModified(saveFileEntry)) }
                .maxOrNull()
    }

    override fun toString(): String {
        return "Rom[${console()}] ${simpleFileName()} (${allFiles().joinToString(", ")})"
    }

    companion object {

        fun areEquals(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
            return localRom.romId == remoteRom.romId
        }

    }

}
