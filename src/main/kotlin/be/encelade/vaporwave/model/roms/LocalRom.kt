package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.services.CueParser.parseCueFile
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import java.io.File

class LocalRom(console: String,
               simpleFileName: String,
               romFiles: List<File>,
               saveFiles: List<File>) :
        Rom<File>(console, simpleFileName, romFiles, saveFiles) {

    override fun romFilesSize(): Long {
        return romFiles.sumOf { file -> file.length() }
    }

    override fun toLocalDateTime(entry: File): LocalDateTime {
        return DateTime(entry.lastModified()).toLocalDateTime()
    }

    /**
     * Add all files listed in "cue" files
     */
    fun attachCompanionFiles(): LocalRom {
        val filesToAdd = romFiles
                .filter { romFile -> romFile.extension == "cue" }
                .mapNotNull { cueFile -> parseCueFile(cueFile) }
                .flatMap { cue -> cue.files }

        return addFiles(filesToAdd)
    }

    fun listFilesFromCue(): List<File> {
        return romFiles
                .filter { file -> file.extension == "cue" }
                .mapNotNull { cueFile -> parseCueFile(cueFile) }
                .flatMap { cue -> cue.files }
                .filter { file -> romFiles.contains(file) }
    }

    private fun addFiles(files: List<File>): LocalRom {
        return LocalRom(console, simpleFileName, romFiles + files, saveFiles)
    }

    override fun toString(): String {
        val filesToString = allFiles().map { file -> file.name }.joinToString(", ")
        return "LocalRom[$console] $simpleFileName ($filesToString)"
    }

}
