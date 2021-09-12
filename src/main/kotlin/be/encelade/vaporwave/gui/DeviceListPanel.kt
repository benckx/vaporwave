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

    private var devices = listOf<Device>()

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
        this.devices = devices
        tableModel.rowCount = 0
        devices.forEach { device ->
            tableModel.addRow(listOf("", device.name).toTypedArray())
        }

        refreshStatus()
    }

    private fun refreshStatus() {
        val clients = devices.mapNotNull { device -> DeviceClient.forDevice(device) }

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            tableModel.setValueAt("connecting...", i, 0)
        }

        clients.forEach { client ->
            thread {
                val i = devices.indexOf(client.device)
                val isOnline = client.isReachable()
                tableModel.setValueAt(if (isOnline) "online" else "offline", i, 0)
            }
        }
    }

}
