package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.Rom
import be.encelade.vaporwave.model.roms.RomSyncDiff
import be.encelade.vaporwave.model.roms.RomSyncStatus.*
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
        tableModel.addColumn("save files")
        tableModel.addColumn("rom size")
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
            tableModel.addRow(renderRom("on computer", localRom))
        }
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, syncDiff: RomSyncDiff) {
        tableModel.rowCount = 0
        (localRoms + remoteRoms)
                .map { rom -> (rom.console to rom.simpleFileName) }
                .distinct()
                .sortedBy { pair -> pair.second }
                .sortedBy { pair -> pair.first }
                .forEach { (console, simpleFileName) ->
                    val status = syncDiff.findStatusBy(console, simpleFileName)

                    val rom: Rom<*>? = when (status) {
                        SYNCED -> localRoms.find { localRom -> localRom.matchesBy(console, simpleFileName) }
                        ONLY_ON_LOCAL -> localRoms.find { localRom -> localRom.matchesBy(console, simpleFileName) }
                        ONLY_ON_DEVICE -> remoteRoms.find { remoteRom -> remoteRom.matchesBy(console, simpleFileName) }
                        else -> null
                    }

                    if (status != ROM_STATUS_UNKNOWN && rom != null) {
                        tableModel.addRow(renderRom(status.lowerCase(), rom))
                    }
                }
    }

    private companion object {

        const val SMALL_COLUMNS_WIDTH = 170

        fun renderRom(status: String, rom: Rom<*>): Array<String> {
            val row = mutableListOf<String>()
            row += status
            row += rom.console
            row += rom.simpleFileName
            row += renderFileList(rom.romFiles)
            row += renderFileList(rom.saveFiles)
            row += humanReadableByteCountBin(rom.romFilesSize())
            return row.toTypedArray()
        }

        fun renderFileList(list: List<*>): String {
            return when (list.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${list.size} files"
            }
        }

    }

}
