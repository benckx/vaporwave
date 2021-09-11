package be.encelade.vaporwave.gui

import be.encelade.vaporwave.gui.GuiUtils.humanReadableByteCountBin
import be.encelade.vaporwave.model.LocalRom
import be.encelade.vaporwave.model.RemoteRom
import be.encelade.vaporwave.model.Rom
import be.encelade.vaporwave.model.Rom.Companion.matchesBy
import be.encelade.vaporwave.model.RomSyncStatus
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

        tableModel.addColumn("status")
        tableModel.addColumn("console")
        tableModel.addColumn("name")
        tableModel.addColumn("files")
        tableModel.addColumn("total size")
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
            tableModel.addRow(renderLocalRom("on computer", localRom))
        }
    }

    fun renderAllRoms(localRoms: List<LocalRom>, remoteRoms: List<RemoteRom>, syncStatus: RomSyncStatus) {
        tableModel.rowCount = 0
        (localRoms + remoteRoms)
                .map { rom -> (rom.console to rom.simpleFileName) }
                .distinct()
                .sortedBy { pair -> pair.second }
                .sortedBy { pair -> pair.first }
                .forEach { (console, simpleFileName) ->
                    var status: String? = null
                    var rom: Rom<*>? = null

                    when {
                        syncStatus.isSync(console, simpleFileName) -> {
                            status = "synced"
                            rom = localRoms.find { localRom -> matchesBy(localRom, console, simpleFileName) }
                        }
                        syncStatus.isOnlyOnLocal(console, simpleFileName) -> {
                            status = "on computer"
                            rom = localRoms.find { localRom -> matchesBy(localRom, console, simpleFileName) }
                        }
                        syncStatus.isOnlyOnDevice(console, simpleFileName) -> {
                            status = "on device"
                            rom = remoteRoms.find { remoteRom -> matchesBy(remoteRom, console, simpleFileName) }
                        }
                    }

                    if (status != null && rom != null) {
                        val row = when (rom) {
                            is LocalRom -> renderLocalRom(status, rom)
                            is RemoteRom -> renderRemoteRom(status, rom)
                            else -> listOf("err").toTypedArray()
                        }

                        tableModel.addRow(row)
                    }
                }
    }

    private companion object {

        const val SMALL_COLUMNS_WIDTH = 170

        fun renderRom(rom: Rom<*>): Array<String> {
            val row = mutableListOf<String>()
            row += rom.console
            row += rom.simpleFileName
            row += when (rom.entries.size) {
                0 -> "no file"
                1 -> "1 file"
                else -> "${rom.entries.size} files"
            }
            return row.toTypedArray()
        }

        fun renderLocalRom(status: String, localRom: LocalRom): Array<String> {
            val row = mutableListOf<String>()
            row += status
            row += renderRom(localRom as Rom<*>)
            row += humanReadableByteCountBin(localRom.entries.sumOf { file -> file.length() })
            return row.toTypedArray()
        }

        fun renderRemoteRom(status: String, localRom: RemoteRom): Array<String> {
            val row = mutableListOf<String>()
            row += status
            row += renderRom(localRom as Rom<*>)
            row += humanReadableByteCountBin(localRom.entries.sumOf { entry -> entry.fileSize })
            return row.toTypedArray()
        }

    }

}
