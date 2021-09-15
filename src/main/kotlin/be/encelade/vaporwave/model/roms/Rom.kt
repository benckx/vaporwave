package be.encelade.vaporwave.model.roms

import org.joda.time.LocalDateTime

abstract class Rom<T>(val romId: RomId,
                      val romFiles: List<T>,
                      val saveFiles: List<T>) {

    abstract fun romFilesSize(): Long

    abstract fun toLocalDateTime(entry: T): LocalDateTime

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
        return saveFiles.map { saveFile -> toLocalDateTime(saveFile) }.maxOrNull()
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
