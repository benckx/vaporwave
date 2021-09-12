package be.encelade.vaporwave.services

import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.text.Charsets.UTF_8

object ExtensionMap {

    const val EXTENSIONS_MAP_FILE = "data/extensions.csv"
    const val EXTENSION_CELL_SEPARATOR = ';'
    const val EXTENSION_VALUE_SEPARATOR = ','

    private val map: Map<String, List<String>>

    val consoleKeys: Set<String>
    val romExtensions: Set<String>

    init {
        val csvFile = File(EXTENSIONS_MAP_FILE)

        val csvLines = FileUtils
                .readLines(csvFile, UTF_8)
                .map { line -> line.trim() }
                .filterNot { line -> line.isEmpty() }

        map = csvLines.associate { csvRow ->
            val csvCells = csvRow.split(EXTENSION_CELL_SEPARATOR)

            val extensions = csvCells[1]
                    .split(EXTENSION_VALUE_SEPARATOR)
                    .map { extension -> extension.removePrefix(".") }
                    .map { extension -> extension.trim() }

            csvCells[0] to extensions.distinct().sorted()
        }

        consoleKeys = map.keys
        romExtensions = map.values.flatten().toSet()
    }

    fun getExtensionPerConsole(console: String) = map[console].orEmpty()

}
