package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.roms.RomId
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class RomCollectionRightClickMenu : JPopupMenu() {

    private val uploadRomToDeviceItem = JMenuItem("Upload rom to device")
    private val uploadSaveFilesToDeviceItem = JMenuItem("Upload save files to device")

    init {
        uploadRomToDeviceItem.ui = CustomMenuUI()
        uploadSaveFilesToDeviceItem.ui = CustomMenuUI()

        add(uploadRomToDeviceItem)
        add(uploadSaveFilesToDeviceItem)

        uploadRomToDeviceItem.isEnabled = false
        uploadSaveFilesToDeviceItem.isEnabled = false
    }

    fun format(selectedRomIds: List<RomId>, syncStatus: DeviceSyncStatus?) {
        if (syncStatus == null) {
            uploadRomToDeviceItem.isEnabled = false
            uploadSaveFilesToDeviceItem.isEnabled = false
        } else {
            uploadRomToDeviceItem.isEnabled = true
            uploadSaveFilesToDeviceItem.isEnabled = true
        }
    }

}
