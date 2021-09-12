package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.roms.Cue
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.File.separator
import kotlin.text.Charsets.UTF_8

object CueParser {

    fun parseCueFile(cueFile: File): Cue? {
        val files = mutableListOf<File>()
        val containingFolder = cueFile.absolutePath.split(separator).dropLast(1).joinToString(separator)

        return if (cueFile.exists()) {
            FileUtils
                    .readLines(cueFile, UTF_8)
                    .filter { line -> line.startsWith("FILE") }
                    .forEach { line ->
                        val firstDoubleQuote = line.indexOfFirst { c -> c == '"' }
                        val lastDoubleQuote = line.indexOfLast { c -> c == '"' }
                        if (firstDoubleQuote < lastDoubleQuote) {
                            val fileName = line.substring(firstDoubleQuote + 1, lastDoubleQuote)
                            val file = File("$containingFolder$separator$fileName")
                            if (file.exists()) {
                                files += file
                            }
                        }
                    }

            Cue(cueFile, files)
        } else {
            null
        }
    }

}
