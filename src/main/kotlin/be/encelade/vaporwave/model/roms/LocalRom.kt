package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.services.CueParser.parseCueFile
import java.io.File

class LocalRom(console: String, simpleFileName: String, romFiles: List<File>) :
        Rom<File>(console, simpleFileName, romFiles) {

    override fun romFilesSize(): Long {
        return romFiles.sumOf { file -> file.length() }
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
        return LocalRom(console, simpleFileName, romFiles + files)
    }

    override fun toString(): String {
        return "Local" + super.toString()
    }

}
