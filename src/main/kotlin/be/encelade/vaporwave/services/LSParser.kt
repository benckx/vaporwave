package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LsEntry
import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.services.ExtensionMap.romExtensions
import org.joda.time.format.DateTimeFormat

object LSParser {

    private val parser = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS Z").withZoneUTC()

    fun parseLsResult(result: String): List<LsEntry> {
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

    fun findRemoveRoms(entries: List<LsEntry>): List<RemoteRom> {
        return entries
                .filter { entry -> entry.isConsole() }
                .filter { entry -> romExtensions.contains(entry.extension()) }
                .groupBy { entry -> entry.console()!! }
                .flatMap { (console, consoleEntries) ->
                    consoleEntries
                            .groupBy { it.simpleFileName() }
                            .map { (fileName, entries) -> RemoteRom(console, fileName, entries) }
                }
    }

}
