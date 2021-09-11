package be.encelade.vaporwave.model

import be.encelade.vaporwave.services.CueParser.parseCueFile
import java.io.File

class LocalRom(console: String, simpleFileName: String, entries: List<File>) :
        Rom<File>(console, simpleFileName, entries) {

    /**
     * Playstation *.cue files also have a bin (large)
     */
    fun attachCompanionFiles(): LocalRom {
        if (entries.size == 1 && entries.first().extension == "cue") {
            parseCueFile(entries.first())?.let { cue ->
                return addFiles(cue.files)
            }
        }

        return this
    }

    private fun addFiles(files: List<File>): LocalRom {
        return LocalRom(console, simpleFileName, entries + files)
    }

    override fun toString(): String {
        return "Local" + super.toString()
    }

}
