package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.Rom
import be.encelade.vaporwave.model.roms.RomSyncDiff
import be.encelade.vaporwave.model.roms.RomSyncStatus.*
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
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
        val titleColumn = 2

        (0 until tableModel.columnCount)
                .filterNot { it == titleColumn }
                .map { i -> table.columnModel.getColumn(i) }
                .forEach { column ->
                    column.maxWidth = SMALL_COLUMNS_WIDTH
                    column.preferredWidth = SMALL_COLUMNS_WIDTH
                }
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        tableModel.rowCount = 0
        localRoms.forEach { localRom ->
            val saveStatusStr = renderLocalSaveStatus(localRom)
            val row = renderRom("on computer", saveStatusStr, localRom)
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
                        SYNCED -> {
                            localRom = localRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            remoteRom = remoteRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            // TODO: rom exists on both -> compare
                        }
                        ONLY_ON_LOCAL -> {
                            localRom = localRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            localRom?.let { rom -> saveStatusStr = renderLocalSaveStatus(rom) }
                        }
                        ONLY_ON_DEVICE -> {
                            remoteRom = remoteRoms.find { rom -> rom.matchesBy(console, simpleFileName) }
                            remoteRom?.let { rom -> saveStatusStr = renderRemoveSaveStatus(rom) }
                        }
                        else -> {
                        }
                    }

                    if (romSyncStatus != ROM_STATUS_UNKNOWN && (localRom != null || remoteRom != null)) {
                        val rom = listOfNotNull(localRom, remoteRom).first()
                        val row = renderRom(romSyncStatus.lowerCase(), saveStatusStr, rom)
                        tableModel.addRow(row)
                    }
                }
    }

    private companion object {

        const val SMALL_COLUMNS_WIDTH = 170

        fun renderRom(romStatus: String, saveStatus: String, rom: Rom<*>): Array<String> {
            val row = mutableListOf<String>()
            row += romStatus
            row += rom.console
            row += rom.simpleFileName
            row += renderFileList(rom.romFiles)
            row += humanReadableByteCountBin(rom.romFilesSize())
            row += saveStatus
            return row.toTypedArray()
        }

        fun renderFileList(list: List<*>): String {
            return when (list.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${list.size} files"
            }
        }

        fun renderLocalSaveStatus(localRom: LocalRom): String {
            return if (localRom.saveFiles.isNotEmpty()) {
                val lastModified = DateTime(localRom.saveFiles.maxOf { file -> file.lastModified() })
                "last modified $lastModified"
            } else {
                "no save"
            }
        }

        fun renderRemoveSaveStatus(remoteRom: RemoteRom): String {
            return if (remoteRom.saveFiles.isNotEmpty()) {
                val lastModified = DateTime(remoteRom.saveFiles.maxOf { file -> file.lastModified })
                "last modified $lastModified"
            } else {
                "no save"
            }
        }

    }

}
