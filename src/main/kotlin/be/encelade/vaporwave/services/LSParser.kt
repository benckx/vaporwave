package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.RemoteEntry
import org.joda.time.format.DateTimeFormat

class LSParser {

    private val parser = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS Z").withZoneUTC()

    fun parse(result: String): List<RemoteEntry> {
        return result
                .split("\n")
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { rawEntry ->
                    val split = rawEntry.split(" ").map { it.trim() }.filterNot { it.isEmpty() }
                    val iso = listOf(5, 6, 7).map { split[it] }.joinToString(" ")
                    val dateTime = parser.parseDateTime(iso)
                    val i = split.indexOfFirst { it.startsWith("/roms/") }
                    val filePath = split.subList(i, split.size).joinToString(" ").removePrefix("/roms")

                    RemoteEntry(dateTime, filePath)
                }
    }

}
