package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.GuiUtils.createBorder
import be.encelade.vaporwave.gui.api.AddDevicePanelCallback
import be.encelade.vaporwave.model.devices.SshConnection
import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.lang3.StringUtils.isNumeric
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.SOUTH
import java.awt.GridLayout
import java.awt.Rectangle
import javax.swing.*

class AddDeviceWindow(parentBound: Rectangle, callback: AddDevicePanelCallback) : JFrame(), LazyLogging {

    private val nameTextField = JTextField("new device")
    private val userTextField = JTextField("ark")
    private val passwordTextField = JTextField("ark")
    private val hostTextField = JTextField("192.168.x.x")
    private val portTextField = JTextField("22")

    private val testConnectionButton = JButton("Test Connection")
    private val addDeviceButton = JButton("Add Device")

    private val formPanel = JPanel()
    private val buttonPanel = JPanel()

    init {
        val width = 400
        val height = 280

        // place this window in the middle of the main one
        val x = parentBound.x + (parentBound.width / 2) - (width / 2)
        val y = parentBound.y + (parentBound.height / 2) - (height / 2)

        title = "Add Device"
        setBounds(x, y, width, height)
        add(formPanel, CENTER)
        add(buttonPanel, SOUTH)

        val nameLabel = JLabel("name")
        val userLabel = JLabel("user")
        val passwordLabel = JLabel("password")
        val hostLabel = JLabel("host")
        val portLabel = JLabel("port")

        formPanel.layout = GridLayout(0, 2)
        formPanel.border = createBorder(left = 10, right = 10, top = 10)
        formPanel.add(nameLabel)
        formPanel.add(nameTextField)
        formPanel.add(userLabel)
        formPanel.add(userTextField)
        formPanel.add(passwordLabel)
        formPanel.add(passwordTextField)
        formPanel.add(hostLabel)
        formPanel.add(hostTextField)
        formPanel.add(portLabel)
        formPanel.add(portTextField)

        buttonPanel.layout = GridLayout(0, 2)
        buttonPanel.add(testConnectionButton)
        buttonPanel.add(addDeviceButton)
        buttonPanel.border = createBorder(left = 10, right = 10, top = 10, bottom = 10)

        testConnectionButton.addActionListener {
            val errors = validateSshConnection()
            if (errors.isNotEmpty()) {
                // TODO: show error in message
                errors.forEach { error -> logger.error(error) }
            } else {
                callback.testConnectionButtonClicked(toSshConnection())
            }
        }

        addDeviceButton.addActionListener {
            val errors = validateSshConnection()
            if (errors.isNotEmpty()) {
                // TODO: show error in message
                errors.forEach { error -> logger.error(error) }
            } else {
                callback.addDeviceButtonClicked(nameTextField.text, toSshConnection())
            }
        }

        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = false
        isAlwaysOnTop = true
        isVisible = true
    }

    private fun validateSshConnection(): List<String> {
        val errors = mutableListOf<String>()
        if (!isNumeric(portTextField.text)) {
            errors += "port must be a number"
        }
        if (!validateIpAddress(hostTextField.text)) {
            errors += "invalid IP"
        }
        return errors
    }

    private fun toSshConnection(): SshConnection {
        return SshConnection(
                username = userTextField.text,
                password = passwordTextField.text,
                host = hostTextField.text,
                port = portTextField.text.toInt()
        )
    }

    private companion object {

        fun validateIpAddress(ip: String): Boolean {
            val numbers = ip.split('.')

            return if (numbers.size == 4) {
                numbers.all { number ->
                    isNumeric(number) && number.toInt() >= 0 && number.toInt() <= 255
                }
            } else {
                false
            }
        }

    }

}
