package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.gui.ListenerExtensions.addTableHeaderClickListener
import be.encelade.vaporwave.gui.comparators.ConsoleComparator
import be.encelade.vaporwave.gui.comparators.RomSizeComparator
import be.encelade.vaporwave.gui.comparators.SimpleFileNameComparator
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.model.roms.RomSyncStatus
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_ONLY_ON_COMPUTER
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_STATUS_UNKNOWN
import be.encelade.vaporwave.model.save.SaveSyncStatus
import be.encelade.vaporwave.model.save.SaveSyncStatus.NO_SAVE_FOUND
import be.encelade.vaporwave.model.save.SaveSyncStatus.SAVE_ONLY_ON_COMPUTER
import be.encelade.vaporwave.utils.LazyLogging
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

internal class RomCollectionPanel : JPanel(), LazyLogging {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    private var renderedLocalRoms: List<LocalRom>? = null
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null

    private var sortColumn: String? = null
    private var asc: Boolean = true
    private val comparatorMap = mutableMapOf<String, Comparator<RomRow>>()

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

        comparatorMap["name"] = SimpleFileNameComparator()
        comparatorMap["console"] = ConsoleComparator()
        comparatorMap["rom size"] = RomSizeComparator()

        val titleColumnIndex = 2
        table.columnModel.getColumn(titleColumnIndex).preferredWidth = TITLE_COLUMN_DEFAULT_WIDTH

        table.addTableHeaderClickListener { event, column ->
            logger.debug("clicked on ${column.headerValue} ${event.clickCount} times")
            if (event.clickCount > 1) {
                if (sortColumn == column.headerValue.toString()) {
                    asc = !asc
                } else {
                    sortColumn = column.headerValue.toString()
                }

                if (renderedLocalRoms != null) {
                    render(renderedLocalRoms!!)
                } else if (renderedDeviceSyncStatus != null) {
                    render(renderedDeviceSyncStatus!!)
                }
            }
        }
    }

    fun clearRomsTable() {
        table.clearSelection()
        tableModel.rowCount = 0
    }

    fun render(localRoms: List<LocalRom>) {
        clearRomsTable()
        this.renderedDeviceSyncStatus = null
        this.renderedLocalRoms = localRoms

        val rows = localRoms.map { localRom ->
            val saveSyncStatus = if (localRom.saveFiles.isNotEmpty()) SAVE_ONLY_ON_COMPUTER else NO_SAVE_FOUND
            RomRow(localRom, null, ROM_ONLY_ON_COMPUTER, saveSyncStatus)
        }

        renderSortedRows(rows)
    }

    fun render(syncStatus: DeviceSyncStatus) {
        clearRomsTable()
        this.renderedDeviceSyncStatus = syncStatus
        this.renderedLocalRoms = null

        val rows = syncStatus
                .allRomIds()
                .mapNotNull { romId ->
                    val localRom = syncStatus.findLocalRom(romId)
                    val remoteRom = syncStatus.findRemoteRom(romId)
                    val romSyncStatus = syncStatus.romSyncStatusOf(romId)
                    if ((localRom != null || remoteRom != null) && romSyncStatus != ROM_STATUS_UNKNOWN) {
                        val saveSyncStatus = syncStatus.saveSyncStatusOf(romId)
                        RomRow(localRom, remoteRom, romSyncStatus, saveSyncStatus)
                    } else {
                        null
                    }
                }

        renderSortedRows(rows)
    }

    private fun renderSortedRows(romRows: List<RomRow>) {
        val comparator = comparatorMap[sortColumn]
        val sortedRows =
                if (comparator != null) {
                    if (asc) {
                        romRows.sortedWith(comparator)
                    } else {
                        romRows.sortedWith(comparator).reversed()
                    }
                } else {
                    romRows
                }

        sortedRows.forEach { row -> tableModel.addRow(row.render()) }
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
            row += rom.console()
            row += rom.simpleFileName()
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
