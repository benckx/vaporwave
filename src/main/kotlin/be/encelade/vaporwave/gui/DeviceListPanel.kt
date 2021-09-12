package be.encelade.vaporwave.gui

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.lang3.BooleanUtils.isTrue
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel.SINGLE_SELECTION
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
        tableModel.addColumn("name")

        table.columnModel.getColumn(0).maxWidth = 170
        table.columnModel.getColumn(0).preferredWidth = 170

        val selectionModel = table.selectionModel
        selectionModel.selectionMode = SINGLE_SELECTION

        selectionModel.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (table.selectedRow >= 0) {
                    val device = devices[table.selectedRow]
                    if (isTrue(deviceToStatus[device])) {
                        callback.onlineDeviceSelected(device)
                    } else {
                        callback.offlineDeviceSelected(device)
                    }

                    unSelectButton.isEnabled = true
                } else {
                    unSelectButton.isEnabled = false
                    callback.noDeviceSelected()
                }
            }
        }

        buttonPanel.layout = GridLayout(0, 1)
        buttonPanel.add(unSelectButton)
        buttonPanel.add(refreshButton)

        unSelectButton.isEnabled = false
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

    // TODO: disable refresh button
    private fun refreshStatus() {
        val clients = devices.mapNotNull { device -> DeviceClient.forDevice(device) }

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            tableModel.setValueAt("trying to connect...", i, 0)
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
