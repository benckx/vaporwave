package be.encelade.vaporwave.model

import be.encelade.vaporwave.services.CueParser.parseCueFile
import java.io.File

class LocalRom(console: String, simpleFileName: String, entries: List<File>) :
        Rom<File>(console, simpleFileName, entries) {

    override fun totalSize(): Long {
        return entries.sumOf { file -> file.length() }
    }
    
    /**
     * Add all files listed in "cue" files
     */
    fun attachCompanionFiles(): LocalRom {
        if (entries.size == 1 && entries.first().extension == "cue") {
            parseCueFile(entries.first())?.let { cue ->
                return addFiles(cue.files)
            }
        }

        return this
    }

    fun listFilesFromCue(): List<File> {
        return entries
                .filter { file -> file.extension == "cue" }
                .mapNotNull { cueFile -> parseCueFile(cueFile) }
                .flatMap { cue -> cue.files }
                .filter { file -> entries.contains(file) }
    }

    private fun addFiles(files: List<File>): LocalRom {
        return LocalRom(console, simpleFileName, entries + files)
    }

    override fun toString(): String {
        return "Local" + super.toString()
    }

}
