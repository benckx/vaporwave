package be.encelade.vaporwave.model

import java.io.File

class LocalRom(console: String, simpleFileName: String, entries: List<File>) :
        Rom<File>(console, simpleFileName, entries) {

    fun addFile(file: File): LocalRom {
        return LocalRom(console, simpleFileName, entries + file)
    }

    /**
     * Playstation *.cue files also have a bin (large)
     */
    // TODO: wrong: open the cue file
    fun attachCompanionFiles(): LocalRom {
        val mustAddBinFile =
                console == "psx" &&
                        entries.size == 1 &&
                        entries.first().extension == "cue" &&
                        matchingFileExist(entries.first(), "bin")

        return if (mustAddBinFile) {
            addFile(matchingFile(entries.first(), "bin"))
        } else {
            this
        }
    }

    override fun toString(): String {
        return "Local" + super.toString()
    }

    private companion object {

        fun matchingFile(file: File, extension: String): File {
            return File(file.absolutePath.removeSuffix(".${file.extension}") + ".$extension")
        }

        fun matchingFileExist(file: File, extension: String): Boolean {
            val matchingFile = matchingFile(file, extension)
            return matchingFile.exists() && !matchingFile.isDirectory
        }

    }

}
