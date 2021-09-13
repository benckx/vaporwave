package be.encelade.vaporwave.gui

import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

internal class ActionPanel(callback: ActionButtonCallback) : JPanel() {

    private val downloadSavesButton = JButton("Download Saves")
    private val uploadSavesButton = JButton("Upload Saves")

    init {
        layout = GridLayout(0, 6)
        add(downloadSavesButton)
        add(uploadSavesButton)
        noOnlineDeviceSelected()

        downloadSavesButton.addActionListener {
            callback.downloadSavesFromDevice()
        }

        uploadSavesButton.addActionListener {
            callback.uploadSavesToDevice()
        }
    }

    fun onlineDeviceSelected() {
        downloadSavesButton.isEnabled = true
        uploadSavesButton.isEnabled = true
    }

    fun noOnlineDeviceSelected() {
        downloadSavesButton.isEnabled = false
        uploadSavesButton.isEnabled = false
    }

}
