package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.LsEntry
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.ExtensionMap.romExtensions
import be.encelade.vaporwave.services.ExtensionMap.saveFilesExtension
import org.joda.time.format.DateTimeFormat

object LSParser {

    private val dateTimeFormat = DateTimeFormat
            .forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS Z")
            .withZoneUTC()

    fun parseLsResult(result: String): List<LsEntry> {
        return result
                .split("\n")
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { rawEntry ->
                    // last modified date time
                    val split = rawEntry.split(" ").map { it.trim() }.filterNot { it.isEmpty() }
                    val iso = listOf(5, 6, 7).map { split[it] }.joinToString(" ")
                    val lastModified = dateTimeFormat.parseDateTime(iso)

                    // size
                    val fileSize = split[4].toLong()

                    // path
                    val i = split.indexOfFirst { it.startsWith("/roms/") }
                    val filePath = split.subList(i, split.size).joinToString(" ").removePrefix("/roms")

                    LsEntry(lastModified, fileSize, filePath)
                }
    }

    fun findRemoteRoms(entries: List<LsEntry>): List<RemoteRom> {
        return entries
                .filter { entry -> entry.isConsole() }
                .filter { entry ->
                    romExtensions.contains(entry.extension()) ||
                            saveFilesExtension.contains(entry.extension())
                }
                .groupBy { entry -> entry.console()!! }
                .flatMap { (console, consoleEntries) ->
                    consoleEntries
                            .groupBy { entry -> entry.simpleFileName() }
                            .map { (fileName, entries) ->
                                val romFiles = entries.filter { entry -> romExtensions.contains(entry.extension()) }
                                val saveFiles = entries.filter { entry -> saveFilesExtension.contains(entry.extension()) }
                                RemoteRom(console, fileName, romFiles, saveFiles)
                            }
                }
    }

}
