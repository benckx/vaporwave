package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LsEntry
import org.joda.time.format.DateTimeFormat

class LSParser {

    private val parser = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS Z").withZoneUTC()

    fun parse(result: String): List<LsEntry> {
        return result
                .split("\n")
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { rawEntry ->
                    // date
                    val split = rawEntry.split(" ").map { it.trim() }.filterNot { it.isEmpty() }
                    val iso = listOf(5, 6, 7).map { split[it] }.joinToString(" ")
                    val dateTime = parser.parseDateTime(iso)

                    // size
                    val fileSize = split[4].toLong()

                    // path
                    val i = split.indexOfFirst { it.startsWith("/roms/") }
                    val filePath = split.subList(i, split.size).joinToString(" ").removePrefix("/roms")

                    LsEntry(dateTime, fileSize, filePath)
                }
    }

}
