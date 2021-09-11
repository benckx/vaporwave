package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.LocalRom
import java.awt.BorderLayout
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
        add(scrollPane, BorderLayout.CENTER)

        tableModel.addColumn("console")
        tableModel.addColumn("name")
        tableModel.addColumn("files")
        tableModel.addColumn("total size")

        listOf(0, 2, 3)
                .map { i -> table.columnModel.getColumn(i) }
                .forEach { column ->
                    column.maxWidth = SMALL_COLUMNS_WIDTH
                    column.preferredWidth = SMALL_COLUMNS_WIDTH
                }
    }

    fun loadRoms(localRoms: List<LocalRom>) {
        tableModel.rowCount = 0
        localRoms.forEach { localRom ->
            val row = mutableListOf<String>()
            row += localRom.console
            row += localRom.simpleFileName
            row += when (localRom.entries.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${localRom.entries.size} files"
            }
            row += humanReadableByteCountBin(localRom.entries.sumOf { file -> file.length() })
            tableModel.addRow(row.toTypedArray())
        }
    }

    private companion object {

        const val SMALL_COLUMNS_WIDTH = 170

    }

}
