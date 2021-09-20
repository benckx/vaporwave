package be.encelade.vaporwave.gui.components

import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.SOUTH
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class AddDevicePanel : JFrame() {

    private val userTextField = JTextField("ark")
    private val passwordTextField = JTextField("ark")
    private val hostTextField = JTextField("192.168.x.x")
    private val portTextField = JTextField("22")

    private val formPanel = JPanel()
    private val buttonPanel = JPanel()

    init {
        val x = 400
        val y = 400
        val width = 400
        val height = 180

        title = "Add Device"
        setBounds(x, y, width, height)
        add(formPanel, CENTER)
        add(buttonPanel, SOUTH)

        val userLabel = JLabel("user")
        val passwordLabel = JLabel("password")
        val hostLabel = JLabel("host")
        val portLabel = JLabel("port")

        formPanel.layout = GridLayout(0, 2)
        formPanel.add(userLabel)
        formPanel.add(userTextField)
        formPanel.add(passwordLabel)
        formPanel.add(passwordTextField)
        formPanel.add(hostLabel)
        formPanel.add(hostTextField)
        formPanel.add(portLabel)
        formPanel.add(portTextField)

        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = false
        isAlwaysOnTop = true
        isVisible = true
    }

}
