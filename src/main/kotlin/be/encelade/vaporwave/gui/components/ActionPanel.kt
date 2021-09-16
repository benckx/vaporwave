package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.api.ActionButtonCallback
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

class ActionPanel(callback: ActionButtonCallback) : JPanel() {

    private val downloadSavesButton = JButton("Download Saves")
    private val uploadSavesButton = JButton("Upload Saves")

    init {
        layout = GridLayout(0, 6)
        add(downloadSavesButton)
        add(uploadSavesButton)
        disableButtons()

        downloadSavesButton.addActionListener {
            callback.downloadSavesFromDevice()
        }

        uploadSavesButton.addActionListener {
            callback.uploadSavesToDevice()
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
