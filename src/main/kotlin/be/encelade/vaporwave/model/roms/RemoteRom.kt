package be.encelade.vaporwave.model.roms

import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class RemoteRom(romId: RomId,
                romFiles: List<LsEntry>,
                saveFiles: List<LsEntry>) :
        Rom<LsEntry>(romId, romFiles, saveFiles) {

    override fun romFilesSize(): Long {
        return romFiles.sumOf { entry -> entry.fileSize }
    }

    override fun toLocalDateTime(entry: LsEntry): LocalDateTime {
        return DateTime(entry.lastModified).toLocalDateTime()
    }

    override fun toString(): String {
        val filesToString = allFiles().map { entry -> entry.filePath }.joinToString(", ")
        return "RemoteRom[${console()}] ${simpleFileName()} ($filesToString)"
    }

}
