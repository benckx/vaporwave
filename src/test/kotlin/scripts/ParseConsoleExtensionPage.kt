package scripts

import be.encelade.vaporwave.services.ExtensionMap.EXTENSIONS_MAP_FILE
import be.encelade.vaporwave.services.ExtensionMap.EXTENSION_CELL_SEPARATOR
import be.encelade.vaporwave.services.ExtensionMap.EXTENSION_VALUE_SEPARATOR
import org.apache.commons.io.FileUtils.writeLines
import org.jsoup.Jsoup
import java.io.File

const val ROM_FOLDER = "Rom Folder:"
const val EXTENSIONS = "Extensions:"

/**
 * Crawl console to roms extensions mapping data from arkos' GitHub wiki
 * and write the result to "extensions.csv"
 */
fun main() {
    val url = "https://github.com/christianhaitian/arkos/wiki/ArkOS-Emulators-and-Ports-information"
    val outputLines = mutableListOf<String>()
    var romFolder = ""

    Jsoup
            .connect(url).get().body()
            .getElementById("wiki-body")!!
            .getElementsByTag("p")
            .flatMap { p -> p.html().replace("<br>", "\n").split("\n") }
            .map { line -> line.trim() }
            .filter { line -> line.startsWith(ROM_FOLDER) || line.startsWith(EXTENSIONS) }
            .forEach { line ->
                if (line.startsWith(ROM_FOLDER)) {
                    romFolder = line.removePrefix(ROM_FOLDER).trim()

                    if (romFolder == "megadrive or genesis") {
                        romFolder = "megadrive"
                    } else if (romFolder == "nes or famicom") {
                        romFolder = "nes"
                    }
                } else {
                    val extensions = line.removePrefix(EXTENSIONS).trim().replace(' ', EXTENSION_VALUE_SEPARATOR)
                    outputLines += romFolder + EXTENSION_CELL_SEPARATOR + extensions
                }
            }

    writeLines(File(EXTENSIONS_MAP_FILE), outputLines.sorted())
}
