package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.GuiUtils.createTitleBorder
import be.encelade.vaporwave.gui.GuiUtils.titleFont
import be.encelade.vaporwave.gui.api.DevicePanelCallback
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.utils.LazyLogging
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.table.DefaultTableModel

class DeviceListPanel(callback: DevicePanelCallback) : JPanel(), LazyLogging {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val scrollPane = JScrollPane(table)

    private val buttonPanel = JPanel()
    private val addDeviceButton = JButton("Add Device")
    private val unSelectButton = JButton("Unselect")
    private val refreshButton = JButton("Refresh")

    init {
        layout = BorderLayout()

        val titleLabel = JLabel("Devices")
        titleLabel.border = createTitleBorder()
        titleLabel.font = titleFont

        add(titleLabel, NORTH)
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
                unSelectButton.isEnabled = table.selectedRow >= 0
                callback.deviceSelected(table.selectedRow)
            }
        }

        buttonPanel.layout = GridLayout(0, 1)
        buttonPanel.add(addDeviceButton)
        buttonPanel.add(unSelectButton)
        buttonPanel.add(refreshButton)

        addDeviceButton.addActionListener {
            callback.addDeviceButtonClicked()
        }

        unSelectButton.isEnabled = false
        unSelectButton.addActionListener {
            table.clearSelection()
            callback.unSelectDeviceButtonClicked()
        }

        refreshButton.addActionListener {
            callback.refreshDevicesButtonClicked()
        }
    }

    fun renderDevices(devices: List<Device>) {
        tableModel.rowCount = 0
        devices.forEach { device ->
            tableModel.addRow(listOf("", device.name).toTypedArray())
        }
    }

    fun setOnlineStatus(isOnlineNow: Boolean, i: Int) {
        tableModel.setValueAt(if (isOnlineNow) "online" else "offline", i, 0)
    }

    fun setConnectingStatus(i: Int) {
        tableModel.setValueAt("trying to connect...", i, 0)
    }

}
