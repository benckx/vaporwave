package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.Rom
import be.encelade.vaporwave.model.roms.RomSyncDiff
import be.encelade.vaporwave.model.roms.RomSyncStatus.*
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.services.SaveComparator.calculateSyncStatus
import org.joda.time.DateTime
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

internal class RomCollectionPanel : JPanel() {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    init {
        layout = BorderLayout()
        add(scrollPane, CENTER)

        tableModel.addColumn("rom status")
        tableModel.addColumn("console")
        tableModel.addColumn("name")
        tableModel.addColumn("rom files")
        tableModel.addColumn("rom size")
        tableModel.addColumn("save status")
        tableModel.addColumn("save last modified")

        val titleColumnIndex = 2
        val saveLastModifiedColumnIndex = 6

        table.columnModel.getColumn(titleColumnIndex).preferredWidth = TITLE_COLUMN_DEFAULT_WIDTH
        table.columnModel.getColumn(saveLastModifiedColumnIndex).preferredWidth = SAVE_LAST_MODIFIED_DEFAULT_WIDTH
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        tableModel.rowCount = 0
        localRoms.forEach { localRom ->
            val saveStatusStr = renderLocalSaveStatus(localRom)
            val row = renderRom("on computer", saveStatusStr, saveLastModified(listOf(localRom)), localRom)
            tableModel.addRow(row)
        }
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, romSyncDiff: RomSyncDiff) {
        tableModel.rowCount = 0
        (localRoms + remoteRoms)
                .sortedWith(ConsoleAndNameRomComparator)
                .map { rom -> (rom.console to rom.simpleFileName) }
                .distinct()
                .forEach { (console, simpleFileName) ->
                    val romSyncStatus = romSyncDiff.findStatusBy(console, simpleFileName)
                    var saveStatusStr = "<unknown>"
                    var localRom: LocalRom? = null
                    var remoteRom: RemoteRom? = null

                    when (romSyncStatus) {
                        ROM_SYNCED -> {
                            localRom = localRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            remoteRom = remoteRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            if (localRom != null && remoteRom != null) {
                                val saveSyncStatus = calculateSyncStatus(localRom, remoteRom)
                                saveStatusStr = saveSyncStatus.lowerCase()
                            }
                        }
                        ROM_ONLY_ON_LOCAL -> {
                            localRom = localRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            localRom?.let { rom -> saveStatusStr = renderLocalSaveStatus(rom) }
                        }
                        ROM_ONLY_ON_DEVICE -> {
                            remoteRom = remoteRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            remoteRom?.let { rom -> saveStatusStr = renderRemoteSaveStatus(rom) }
                        }
                        else -> {
                        }
                    }

                    if (romSyncStatus != ROM_STATUS_UNKNOWN && (localRom != null || remoteRom != null)) {
                        val allRoms = listOfNotNull(localRom, remoteRom)
                        val rom = allRoms.first()
                        val row = renderRom(romSyncStatus.lowerCase(), saveStatusStr, saveLastModified(allRoms), rom)
                        tableModel.addRow(row)
                    }
                }
    }

    private companion object {

        const val TITLE_COLUMN_DEFAULT_WIDTH = 550
        const val SAVE_LAST_MODIFIED_DEFAULT_WIDTH = 220

        fun renderRom(romStatus: String, saveStatus: String, lastModified: DateTime?, rom: Rom<*>): Array<String> {
            val row = mutableListOf<String>()
            row += romStatus
            row += rom.console
            row += rom.simpleFileName
            row += renderFileList(rom.romFiles)
            row += humanReadableByteCountBin(rom.romFilesSize())
            row += saveStatus
            row += lastModified?.toString() ?: "n/a"
            return row.toTypedArray()
        }

        fun renderFileList(list: List<*>): String {
            return when (list.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${list.size} files"
            }
        }

        fun saveLastModified(roms: List<Rom<*>>): DateTime? {
            return roms
                    .mapNotNull { rom ->
                        when (rom) {
                            is LocalRom -> rom.saveFiles.map { file -> DateTime(file.lastModified()) }.maxOrNull()
                            is RemoteRom -> rom.saveFiles.map { file -> file.lastModified }.maxOrNull()
                            else -> null
                        }
                    }
                    .maxOrNull()
        }

        fun renderLocalSaveStatus(localRom: LocalRom): String {
            return if (localRom.saveFiles.isNotEmpty()) {
                renderFileList(localRom.saveFiles)
            } else {
                "no save found"
            }
        }

        fun renderRemoteSaveStatus(remoteRom: RemoteRom): String {
            return if (remoteRom.saveFiles.isNotEmpty()) {
                renderFileList(remoteRom.saveFiles)
            } else {
                "no save found"
            }
        }

    }

}
