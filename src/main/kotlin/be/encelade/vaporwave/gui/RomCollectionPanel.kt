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
    private val list = JTable(tableModel)
    private val scrollPane = JScrollPane(list)

    init {
        layout = BorderLayout()
        add(scrollPane, BorderLayout.CENTER)

        tableModel.addColumn("console")
        tableModel.addColumn("name")
        tableModel.addColumn("files")
        tableModel.addColumn("total size")
    }

    fun loadLocalRoms(localRoms: List<LocalRom>) {
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
            row += humanReadableByteCountBin(localRom.entries.sumOf { it.length() })
            tableModel.addRow(row.toTypedArray())
        }
    }

}
