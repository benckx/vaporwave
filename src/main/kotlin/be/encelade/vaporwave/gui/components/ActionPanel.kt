package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.api.ActionPanelCallback
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

class ActionPanel(callback: ActionPanelCallback) : JPanel() {

    private val downloadSavesButton = JButton("Download Saves")
    private val uploadSavesButton = JButton("Upload Saves")

    init {
        layout = GridLayout(0, 6)
        add(downloadSavesButton)
        add(uploadSavesButton)
        disableButtons()

        downloadSavesButton.addActionListener {
            callback.downloadSavesFromDeviceButtonClicked()
        }

        uploadSavesButton.addActionListener {
            callback.uploadSavesToDeviceButtonClick()
        }
    }

    fun enableButtons() {
        downloadSavesButton.isEnabled = true
        uploadSavesButton.isEnabled = true
    }

    fun disableButtons() {
        downloadSavesButton.isEnabled = false
        uploadSavesButton.isEnabled = false
    }

}