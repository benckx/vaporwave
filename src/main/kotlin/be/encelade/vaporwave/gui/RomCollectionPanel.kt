package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.roms.*
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_ONLY_ON_COMPUTER
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_STATUS_UNKNOWN
import be.encelade.vaporwave.model.roms.comparators.ConsoleAndNameRomComparator
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
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
        table.columnModel.getColumn(titleColumnIndex).preferredWidth = TITLE_COLUMN_DEFAULT_WIDTH
    }

    fun clearRomsTable() {
        table.clearSelection()
        tableModel.rowCount = 0
    }

    fun renderLocalRoms(localRoms: List<LocalRom>) {
        clearRomsTable()

        localRoms.forEach { localRom ->
            val saveSyncStatus = if (localRom.saveFiles.isNotEmpty()) SAVE_ONLY_ON_COMPUTER else NO_SAVE_FOUND
            val row = renderRom(localRom, null, ROM_ONLY_ON_COMPUTER, saveSyncStatus)
            tableModel.addRow(row)
        }
    }

    fun renderForOnlineDevice(localRoms: List<LocalRom>,
                              remoteRoms: List<RemoteRom>,
                              romSyncDiff: RomSyncDiff,
                              saveSyncMap: Map<RomId, SaveSyncStatus>) {
        clearRomsTable()

        (localRoms + remoteRoms)
                .sortedWith(ConsoleAndNameRomComparator)
                .map { rom -> rom.romId() }
                .distinct()
                .forEach { romId ->
                    val localRom = localRoms.find { rom -> rom.matchesBy(romId) }
                    val remoteRom = remoteRoms.find { rom -> rom.matchesBy(romId) }
                    val romSyncStatus = romSyncDiff.findStatusBy(romId)

                    if (romSyncStatus != ROM_STATUS_UNKNOWN && (localRom != null || remoteRom != null)) {
                        val saveSyncStatus = saveSyncMap[romId] ?: SAVE_STATUS_UNKNOWN
                        val row = renderRom(localRom, remoteRom, romSyncStatus, saveSyncStatus)
                        tableModel.addRow(row)
                    }
                }
    }

    private companion object {

        const val TITLE_COLUMN_DEFAULT_WIDTH = 450
        const val NO_VALUE_CELL = "--"

        val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm")

        fun renderRom(localRom: LocalRom?, remoteRom: RemoteRom?, romSyncStatus: RomSyncStatus, saveSyncStatus: SaveSyncStatus): Array<String> {
            val allRoms = listOfNotNull(localRom, remoteRom)
            val rom = allRoms.first()
            val lastModified = rom.saveFileLastModified()

            val row = mutableListOf<String>()
            row += romSyncStatus.lowerCase()
            row += rom.console
            row += rom.simpleFileName
            row += renderFileList(rom.romFiles)
            row += humanReadableByteCountBin(rom.romFilesSize())
            row += if (saveSyncStatus == NO_SAVE_FOUND) {
                NO_VALUE_CELL
            } else {
                saveSyncStatus.lowerCase()
            }
            row += if (lastModified != null) {
                dateFormat.print(lastModified)
            } else {
                NO_VALUE_CELL
            }
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
