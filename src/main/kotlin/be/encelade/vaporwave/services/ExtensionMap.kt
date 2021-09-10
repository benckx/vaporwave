package be.encelade.vaporwave.services

import org.apache.commons.io.FileUtils
import java.io.File

object ExtensionMap {

    const val EXTENSIONS_MAP_FILE = "data/extensions.csv"
    private val map: Map<String, List<String>>

    val consoleKeys: Set<String>
    val romExtensions: Set<String>

    init {
        val file = File(EXTENSIONS_MAP_FILE)
        val lines = FileUtils.readLines(file, "UTF-8").map { it.trim() }.filterNot { it.isEmpty() }
        map = lines.associate { entry ->
            val split = entry.split(";")
            val extensions = split[1].split(",").map { it.removePrefix(".") }.map { it.trim() }
            split[0] to extensions.distinct().sorted()
        }

        consoleKeys = map.keys
        romExtensions = map.values.flatten().map { it.removePrefix(".") }.toSet()
    }

    fun getExtensionPerConsole(console: String) = map[console].orEmpty()

}
