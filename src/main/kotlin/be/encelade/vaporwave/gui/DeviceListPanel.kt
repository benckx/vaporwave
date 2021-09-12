package be.encelade.vaporwave.gui

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION
import javax.swing.table.DefaultTableModel
import kotlin.concurrent.thread

internal class DeviceListPanel(callback: DeviceSelectionGuiCallback) : JPanel(), LazyLogging {

    private var devices = listOf<Device>()
    private val deviceToStatus = mutableMapOf<Device, Boolean>()

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    private val buttonPanel = JPanel()
    private val unSelectButton = JButton("Unselect")
    private val refreshButton = JButton("Refresh")

    init {
        layout = BorderLayout()
        add(scrollPane, CENTER)
        add(buttonPanel, EAST)
        preferredSize = Dimension(800, 150)

        tableModel.addColumn("status")
        tableModel.addColumn("id")

        table.columnModel.getColumn(0).maxWidth = 150
        table.columnModel.getColumn(0).preferredWidth = 150

        val selectionModel = table.selectionModel
        selectionModel.selectionMode = SINGLE_INTERVAL_SELECTION

        selectionModel.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (table.selectedRow < 0) {
                    callback.noDeviceSelected()
                } else {
                    callback.deviceSelected(devices[table.selectedRow])
                }
            }
        }

        buttonPanel.layout = GridLayout(0, 1)
        buttonPanel.add(unSelectButton)
        buttonPanel.add(refreshButton)

        unSelectButton.addActionListener { table.clearSelection() }
        refreshButton.addActionListener { refreshStatus() }
    }

    fun renderDevices(devices: List<Device>) {
        this.devices = devices
        this.deviceToStatus.clear()

        tableModel.rowCount = 0
        devices.forEach { device ->
            tableModel.addRow(listOf("", device.name).toTypedArray())
        }

        refreshStatus()
    }

    private fun refreshStatus() {
        val clients = devices.mapNotNull { device -> DeviceClient.forDevice(device) }

//        deviceToStatus.clear()

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            tableModel.setValueAt("connecting...", i, 0)
        }

        clients.forEach { client ->
            thread {
                val i = devices.indexOf(client.device)
                val isOnlineBefore = deviceToStatus[client.device]
                val isOnlineNow = client.isReachable()
                deviceToStatus[client.device] = isOnlineNow
                tableModel.setValueAt(if (isOnlineNow) "online" else "offline", i, 0)

                if (isOnlineBefore != isOnlineNow) {
                    // TODO: update rom list if device came online and is selected
                }
            }
        }
    }

}
