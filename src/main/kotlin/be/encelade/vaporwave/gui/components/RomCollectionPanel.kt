package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.RomRow
import be.encelade.vaporwave.gui.SwingExtensions.addTableHeaderClickListener
import be.encelade.vaporwave.gui.SwingExtensions.listColumns
import be.encelade.vaporwave.gui.api.TableEventCallback
import be.encelade.vaporwave.gui.comparators.*
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_ONLY_ON_COMPUTER
import be.encelade.vaporwave.model.roms.RomSyncStatus.ROM_STATUS_UNKNOWN
import be.encelade.vaporwave.model.save.SaveSyncStatus.NO_SAVE_FOUND
import be.encelade.vaporwave.model.save.SaveSyncStatus.SAVE_ONLY_ON_COMPUTER
import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumn

class RomCollectionPanel(rightClickMenu: RomCollectionRightClickMenu,
                         private val callback: TableEventCallback) : JPanel(), LazyLogging {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    private var sortColumn: String? = null
    private var asc: Boolean = true
    private val comparatorMap = mutableMapOf<String, Comparator<RomRow>>()

    init {
        layout = BorderLayout()
        add(scrollPane, CENTER)

        table.componentPopupMenu = rightClickMenu
        tableModel.addColumn("rom status")
        tableModel.addColumn("console")
        tableModel.addColumn("name")
        tableModel.addColumn("rom files")
        tableModel.addColumn("rom size")
        tableModel.addColumn("save status")
        tableModel.addColumn("save last modified")

        comparatorMap["rom status"] = RomStatusComparator()
        comparatorMap["console"] = ConsoleComparator()
        comparatorMap["name"] = SimpleFileNameComparator()
        comparatorMap["rom files"] = NbrOfRomFilesComparator()
        comparatorMap["rom size"] = RomSizeComparator()
        comparatorMap["save status"] = SaveStatusComparator()
        comparatorMap["save last modified"] = LastUpdatedSaveFileComparator()

        val titleColumnIndex = 2
        table.columnModel.getColumn(titleColumnIndex).preferredWidth = TITLE_COLUMN_DEFAULT_WIDTH

        table.addTableHeaderClickListener { event, column ->
            val noArrowHeader = headerValueNoArrows(column)
            logger.debug("clicked on $noArrowHeader ${event.clickCount} times")
            if (sortColumn == noArrowHeader) {
                asc = !asc
            } else {
                sortColumn = noArrowHeader
                asc = true
            }

            clearHeaderArrows()
            column.headerValue = "$noArrowHeader ${arrow(asc)}"
            callback.headerColumnClicked()
        }

        table.selectionModel.addListSelectionListener {
            callback.tableSelectionChanged()
        }
    }

    private fun clearHeaderArrows() {
        table.listColumns().forEach { column ->
            column.headerValue = headerValueNoArrows(column)
        }
    }

    fun clearTable() {
        table.clearSelection()
        tableModel.rowCount = 0
    }

    fun render(localRoms: List<LocalRom>) {
        clearTable()

        val rows = localRoms.map { localRom ->
            val saveSyncStatus = if (localRom.saveFiles.isNotEmpty()) SAVE_ONLY_ON_COMPUTER else NO_SAVE_FOUND
            RomRow(localRom, null, ROM_ONLY_ON_COMPUTER, saveSyncStatus)
        }

        renderSortedRows(rows)
    }

    fun render(syncStatus: DeviceSyncStatus) {
        clearTable()
//        this.renderedDeviceSyncStatus = syncStatus

        val rows = syncStatus
                .allRomIds()
                .mapNotNull { romId ->
                    val localRom = syncStatus.findLocalRom(romId)
                    val remoteRom = syncStatus.findRemoteRom(romId)
                    val romSyncStatus = syncStatus.romSyncStatus(romId)
                    if ((localRom != null || remoteRom != null) && romSyncStatus != ROM_STATUS_UNKNOWN) {
                        val saveSyncStatus = syncStatus.saveSyncStatus(romId)
                        RomRow(localRom, remoteRom, romSyncStatus, saveSyncStatus)
                    } else {
                        null
                    }
                }

        renderSortedRows(rows)
    }

    private fun renderSortedRows(rows: List<RomRow>) {
        // sorted by console and name first
        var sortedRows = rows.sortedWith(ConsoleAndNameComparator())

        // if column header has been selected for sort
        comparatorMap[sortColumn]?.let { comparator ->
            sortedRows = rows.sortedWith(comparator)
            if (!asc) {
                sortedRows = sortedRows.reversed()
            }
        }

        sortedRows.forEach { row -> tableModel.addRow(row.render()) }
    }

    fun listSelectedRoms(): List<RomId> {
        return table
                .selectedRows
                .map { i ->
                    val console = tableModel.getValueAt(i, 1).toString()
                    val simpleFileName = tableModel.getValueAt(i, 2).toString()
                    RomId(console, simpleFileName)
                }
    }

    private companion object {

        const val TITLE_COLUMN_DEFAULT_WIDTH = 450
        const val ASC_ARROW = "▲"
        const val DESC_ARROW = "▼"

        fun arrow(asc: Boolean): String {
            return if (asc) ASC_ARROW else DESC_ARROW
        }

        fun headerValueNoArrows(column: TableColumn): String {
            return column
                    .headerValue
                    .toString()
                    .removeSuffix(DESC_ARROW)
                    .removeSuffix(ASC_ARROW)
                    .trim()
        }

    }

}
