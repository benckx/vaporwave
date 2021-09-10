import be.encelade.vaporwave.services.ExtensionMap.EXTENSIONS_MAP_FILE
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import java.io.File

const val ROM_FOLDER = "Rom Folder:"
const val EXTENSIONS = "Extensions:"

fun main() {
    val url = "https://github.com/christianhaitian/arkos/wiki/ArkOS-Emulators-and-Ports-information"
    val outputLines = mutableListOf<String>()
    var romFolder = ""

    Jsoup
            .connect(url).get().body()
            .getElementById("wiki-body")!!
            .getElementsByTag("p")
            .flatMap { it.html().replace("<br>", "\n").split("\n") }
            .map { it.trim() }
            .filter { it.startsWith(ROM_FOLDER) || it.startsWith(EXTENSIONS) }
            .forEach { line ->
                if (line.startsWith(ROM_FOLDER)) {
                    romFolder = line.removePrefix(ROM_FOLDER).trim()
                } else {
                    val extensions = line.removePrefix(EXTENSIONS).trim().replace(" ", ",")
                    outputLines += "$romFolder;$extensions"
                }
            }

    FileUtils.writeLines(File(EXTENSIONS_MAP_FILE), outputLines)

}
