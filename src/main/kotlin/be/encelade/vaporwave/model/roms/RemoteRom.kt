package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.model.LsEntry
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class RemoteRom(console: String,
                simpleFileName: String,
                romFiles: List<LsEntry>,
                saveFiles: List<LsEntry>) :
        Rom<LsEntry>(console, simpleFileName, romFiles, saveFiles) {

    override fun romFilesSize(): Long {
        return romFiles.sumOf { entry -> entry.fileSize }
    }

    override fun toLocalDateTime(entry: LsEntry): LocalDateTime {
        return DateTime(entry.lastModified).toLocalDateTime()
    }

    override fun toString(): String {
        val filesToString = allFiles().map { entry -> entry.filePath }.joinToString(", ")
        return "RemoteRom[$console] $simpleFileName ($filesToString)"
    }

}
