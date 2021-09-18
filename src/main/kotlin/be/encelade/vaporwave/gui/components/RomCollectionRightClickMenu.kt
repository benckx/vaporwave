package be.encelade.vaporwave.gui.components

import be.encelade.vaporwave.gui.CustomItemMenuUI
import be.encelade.vaporwave.gui.SwingExtensions.createEmptyBorder
import be.encelade.vaporwave.gui.api.RightClickMenuCallback
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.roms.RomId
import be.encelade.vaporwave.utils.CollectionUtils.exists
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class RomCollectionRightClickMenu(callback: RightClickMenuCallback) : JPopupMenu() {

    private val downloadRomsFromDeviceItem = JMenuItem("Download rom(s) from device")
    private val downloadSaveFilesFromDeviceItem = JMenuItem("Download save file(s) from device")
    private val uploadRomsToDeviceItem = JMenuItem("Upload rom(s) to device")
    private val uploadSaveFilesToDeviceItem = JMenuItem("Upload save file(s) to device")

    private val allItems = listOf(
            downloadRomsFromDeviceItem,
            downloadSaveFilesFromDeviceItem,
            uploadRomsToDeviceItem,
            uploadSaveFilesToDeviceItem
    )

    init {
        allItems.forEach { item ->
            item.ui = CustomItemMenuUI()
            item.border = createEmptyBorder(top = 3, bottom = 3, left = 5, right = 5)
            item.isEnabled = false
        }

        add(downloadRomsFromDeviceItem)
        add(downloadSaveFilesFromDeviceItem)
        addSeparator()
        add(uploadRomsToDeviceItem)
        add(uploadSaveFilesToDeviceItem)

        downloadRomsFromDeviceItem.addActionListener { callback.downloadSelectedRomsFromDevice() }
        downloadSaveFilesFromDeviceItem.addActionListener { callback.downloadSelectedRomsSaveFilesFromDevice() }
        uploadRomsToDeviceItem.addActionListener { callback.uploadSelectedRomsToDevice() }
        uploadSaveFilesToDeviceItem.addActionListener { callback.uploadSelectedRomsSaveFilesToDevice() }
    }

    /**
     * Calculate which [JMenuItem] should be enabled, given the [RomId] selected in the UI.
     */
    fun updateEnabledItems(romIds: List<RomId>, syncStatus: DeviceSyncStatus?) {
        if (syncStatus == null) {
            allItems.forEach { item -> item.isEnabled = false }
        } else {
            downloadRomsFromDeviceItem.isEnabled = romIds.exists { syncStatus.romSyncStatus(it).canDownloadFromDevice() }
            downloadSaveFilesFromDeviceItem.isEnabled = romIds.exists { syncStatus.saveSyncStatus(it).canDownloadFromDevice() }
            uploadRomsToDeviceItem.isEnabled = romIds.exists { syncStatus.romSyncStatus(it).canUploadOnDevice() }
            uploadSaveFilesToDeviceItem.isEnabled = romIds.exists { syncStatus.saveSyncStatus(it).canUploadOnDevice() }
        }
    }

}
