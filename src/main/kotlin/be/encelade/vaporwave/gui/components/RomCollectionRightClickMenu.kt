package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.CustomItemMenuUI
import be.encelade.vaporwave.gui.SwingExtensions.createEmptyBorder
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.utils.CollectionUtils.exists
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class RomCollectionRightClickMenu : JPopupMenu() {

    private val uploadRomToDeviceItem = JMenuItem("Upload rom(s) to device")
    private val uploadSaveFilesToDeviceItem = JMenuItem("Upload save file(s) to device")

    private val allItems = listOf(
            uploadRomToDeviceItem,
            uploadSaveFilesToDeviceItem
    )

    init {
        allItems.forEach { item ->
            item.ui = CustomItemMenuUI()
            item.border = createEmptyBorder(top = 3, bottom = 3, left = 5, right = 5)
        }

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
            uploadRomToDeviceItem.isEnabled = selectedRomIds.exists { syncStatus.romSyncStatus(it).canUploadOnDevice() }
            uploadSaveFilesToDeviceItem.isEnabled = selectedRomIds.exists { syncStatus.saveSyncStatus(it).canUploadOnDevice() }
        }
    }

}
