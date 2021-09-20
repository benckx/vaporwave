package be.encelade.vaporwave.services

import be.encelade.vaporwave.model.roms.LsEntry
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.services.ExtensionMap.romExtensions
import be.encelade.vaporwave.services.ExtensionMap.saveFilesExtension
import be.encelade.vaporwave.utils.LazyLogging
import be.encelade.vaporwave.utils.TimeUtils.commandDateTimeFormat

object CommandResultParser : LazyLogging {

    fun parseLsResult(commandResult: String): List<LsEntry> {
        return commandResult
                .split("\n")
                .map { it.trim() }
                .filterNot { it.isEmpty() }
                .map { rawEntry ->
                    // last modified date time
                    val split = rawEntry.split(" ").map { it.trim() }.filterNot { it.isEmpty() }
                    val iso = listOf(5, 6, 7).map { split[it] }.joinToString(" ")
                    val lastModified = commandDateTimeFormat.parseDateTime(iso)

                    // size
                    val fileSize = split[4].toLong()

                    // path
                    val i = split.indexOfFirst { it.startsWith("/roms/") }
                    val filePath = split.subList(i, split.size).joinToString(" ")

                    LsEntry(lastModified, fileSize, filePath)
                }
    }

    fun parseMd5Result(commandResult: String): Map<String, String> {
        val fileToHashMap = mutableMapOf<String, String>()

        commandResult
                .split("\n")
                .map { line ->
                    val hash = line.split(" ").first()
                    val filePath = line.removePrefix(hash).trim()
                    fileToHashMap[filePath] = hash
                }

        return fileToHashMap
    }

    fun lsEntriesToRemoteRoms(entries: List<LsEntry>, md5Map: Map<String, String>): List<RemoteRom> {
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
                                val saveFiles = entries
                                        .filter { lsEntry -> saveFilesExtension.contains(lsEntry.extension()) }
                                        .filter { lsEntry -> md5Map.containsKey(lsEntry.filePath) }
                                        .map { lsEntry -> lsEntry to md5Map[lsEntry.filePath]!! }

                                RemoteRom(RomId(console, fileName), romFiles, saveFiles)
                            }
                }
    }

}
