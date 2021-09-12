package be.encelade.vaporwave.gui

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.devices.Device
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import kotlin.concurrent.thread

internal class DeviceListPanel : JPanel() {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    init {
        layout = BorderLayout()
        add(scrollPane, CENTER)
        preferredSize = Dimension(800, 150)

        tableModel.addColumn("status")
        tableModel.addColumn("id")

        table.columnModel.getColumn(0).maxWidth = 150
        table.columnModel.getColumn(0).preferredWidth = 150
    }

    fun renderDevices(devices: List<Device>) {
        tableModel.rowCount = 0
        devices.forEach { device ->
            tableModel.addRow(listOf("connecting...", device.name).toTypedArray())
        }

        devices.forEach { device ->
            thread {
                DeviceClient.forDevice(device)?.let { client ->
                    val i = devices.indexOf(device)
                    val isOnline = client.isReachable()
                    tableModel.setValueAt(if (isOnline) "online" else "offline", i, 0)
                }
            }
        }
    }

}
