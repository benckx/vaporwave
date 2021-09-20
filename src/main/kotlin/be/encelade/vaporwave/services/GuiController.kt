package be.encelade.vaporwave.services

import be.encelade.vaporwave.clients.DeviceClient
import be.encelade.vaporwave.clients.SshClient
import be.encelade.vaporwave.gui.api.*
import be.encelade.vaporwave.gui.components.*
import be.encelade.vaporwave.model.DeviceSyncStatus
import be.encelade.vaporwave.model.devices.Device
import be.encelade.vaporwave.model.devices.SshConnection
import be.encelade.vaporwave.model.devices.SshDevice
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.LsEntry
import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.persistence.DeviceManager
import be.encelade.vaporwave.utils.FileUtils.setLastModified
import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.lang3.BooleanUtils.isTrue
import java.io.File
import javax.swing.JOptionPane.*
import kotlin.concurrent.thread

/**
 * Logic executed behind the GUI elements.
 */
class GuiController(private val deviceManager: DeviceManager,
                    private val localRomManager: LocalRomManager) :
        DevicePanelCallback,
        AddDevicePanelCallback,
        RomCollectionCallback,
        RightClickMenuCallback,
        ActionPanelCallback,
        LazyLogging {

    // gui components
    private val deviceListPanel = DeviceListPanel(this)
    private var addDeviceWindow: AddDeviceWindow? = null
    private val rightClickMenu = RomCollectionRightClickMenu(this)
    private val romCollectionPanel = RomCollectionPanel(rightClickMenu, this)
    private val actionPanel = ActionPanel(this)
    private val mainWindow = MainWindow(deviceListPanel, romCollectionPanel, actionPanel)

    // shown states
    private var devices = listOf<Device>()
    private val isOnlineMap = mutableMapOf<Device, Boolean>()
    private var selectedDevice: Device? = null
    private var renderedLocalRoms: List<LocalRom>? = null
    private var renderedDeviceSyncStatus: DeviceSyncStatus? = null

    init {
        devices = deviceManager.loadDevices()
        deviceListPanel.renderDevices(devices)

        renderLocalRoms()
        refreshOnlineStatus()
    }

    fun start() {
        mainWindow.isVisible = true
    }

    override fun deviceSelected(idx: Int) {
        if (idx >= 0) {
            this.selectedDevice = devices[idx]
            if (isTrue(isOnlineMap[devices[idx]])) {
                // online device -> render device sync
                renderDeviceSyncStatus()
                actionPanel.enableButtons()
            } else {
                // offline device -> empty table
                romCollectionPanel.clearTable()
                actionPanel.disableButtons()
            }
        } else {
            // no device selected -> render local roms
            this.selectedDevice = null
            renderLocalRoms()
            actionPanel.disableButtons()
        }
    }

    override fun addDeviceButtonClicked() {
        addDeviceWindow = AddDeviceWindow(mainWindow.bounds, this)
    }

    override fun refreshDevicesButtonClicked() {
        refreshOnlineStatus()
    }

    override fun unSelectDeviceButtonClicked() {
        this.selectedDevice = null
        renderLocalRoms()
    }

    override fun testConnectionButtonClicked(sshConnection: SshConnection) {
        val client = SshClient(sshConnection)
        if (client.isReachable()) {
            showMessageDialog(addDeviceWindow, "Connection Successful!", "Test Connection", INFORMATION_MESSAGE)
        } else {
            showMessageDialog(addDeviceWindow, "Connection Failed", "Test Connection", WARNING_MESSAGE)
        }
    }

    override fun addDeviceButtonClicked(name: String, sshConnection: SshConnection) {
        val device = SshDevice(name, sshConnection)
        devices = devices + device
        deviceManager.saveDevices(devices)
        deviceListPanel.renderDevices(devices)
        refreshOnlineStatus()
        addDeviceWindow?.let { window -> window.isVisible = false }
    }

    override fun romTableHeaderColumnClicked() {
        // Re-render what is already being displayed, without fetching or calculating
        renderedLocalRoms?.let { romCollectionPanel.render(it) }
        renderedDeviceSyncStatus?.let { romCollectionPanel.render(it) }
    }

    override fun romTableSelectionChanged() {
        val selectedRomIds = romCollectionPanel.listSelectedRomIds()
        rightClickMenu.updateEnabledItems(selectedRomIds, renderedDeviceSyncStatus)
    }

    override fun downloadSelectedRomsFromDevice() {
        logger.debug("download roms from device")
        downloadSelectedRomFilesFromDevice { localRom -> localRom.allFiles() }
    }

    override fun downloadSelectedRomsSaveFilesFromDevice() {
        logger.debug("download save files from device")
        downloadSelectedRomFilesFromDevice { localRom -> localRom.saveFilesWithoutHash() }
    }

    override fun uploadSelectedRomsToDevice() {
        logger.debug("upload roms to device")
        uploadSelectedRomFilesToDevice { localRom -> localRom.allFiles() }
    }

    override fun uploadSelectedRomsSaveFilesToDevice() {
        logger.debug("upload save files to device")
        uploadSelectedRomFilesToDevice { localRom -> localRom.saveFilesWithoutHash() }
    }

    override fun downloadSavesFromDeviceButtonClicked() {
        logger.warn("TODO: downloadSavesFromDeviceButtonClicked")
    }

    override fun uploadSavesToDeviceButtonClick() {
        logger.warn("TODO: uploadSavesToDevice")
    }

    private fun refreshOnlineStatus() {
        val clients = devices.map { device -> DeviceClient.forDevice(device) }

        clients.forEach { client ->
            val i = devices.indexOf(client.device)
            deviceListPanel.setConnectingStatus(i)
        }

        clients.forEach { client ->
            thread {
                val i = devices.indexOf(client.device)
                val isDeviceOnlineBefore = isOnlineMap[client.device]
                val isDeviceOnlineNow = client.isReachable()
                isOnlineMap[client.device] = isDeviceOnlineNow
                deviceListPanel.setOnlineStatus(isDeviceOnlineNow, i)

                if (isDeviceOnlineBefore != isDeviceOnlineNow) {
                    // TODO: update rom list if device came online and is selected
                }
            }
        }
    }

    private fun renderLocalRoms() {
        val localRoms = localRomManager.listLocalRoms()
        romCollectionPanel.render(localRoms)
        this.renderedLocalRoms = localRoms
        this.renderedDeviceSyncStatus = null
    }

    // TODO: we could also fetch each device status async, in the background
    private fun renderDeviceSyncStatus() {
        selectedDevice?.let { device ->
            val deviceSyncStatus = localRomManager.calculateDeviceSyncStatus(device)
            romCollectionPanel.render(deviceSyncStatus)
            this.renderedLocalRoms = null
            this.renderedDeviceSyncStatus = deviceSyncStatus
        }
    }

    private fun downloadSelectedRomFilesFromDevice(fileSelector: (RemoteRom) -> List<LsEntry>) {
        renderedDeviceSyncStatus?.let { deviceSyncStatus ->
            val entryToFolderPairs: List<Pair<LsEntry, File>> =
                    romCollectionPanel
                            .listSelectedRomIds()
                            .mapNotNull { romId -> deviceSyncStatus.findRemoteRom(romId) }
                            .flatMap { remoteRom ->
                                fileSelector(remoteRom).map { lsEntry ->
                                    val localConsoleFolder = localRomManager.consoleFolder(remoteRom.console())
                                    lsEntry to localConsoleFolder
                                }
                            }

            val filePairs = entryToFolderPairs.map { (lsEntry, folder) -> lsEntry.filePath to folder }
            val client = DeviceClient.forDevice(selectedDevice!!)
            val downloadedFiles = client.downloadFilesFromDevice(filePairs)

            if (downloadedFiles.size == entryToFolderPairs.size) {
                downloadedFiles.indices.forEach { i ->
                    val lsEntry = entryToFolderPairs[i].first
                    downloadedFiles[i].setLastModified(lsEntry.lastModified)
                }
            } else {
                logger.error("inconsistent number of files!")
                downloadedFiles.forEach { file -> file.delete() }
            }

            renderDeviceSyncStatus()
        }
    }

    private fun uploadSelectedRomFilesToDevice(fileSelector: (LocalRom) -> List<File>) {
        renderedDeviceSyncStatus?.let { deviceSyncStatus ->
            val client = DeviceClient.forDevice(selectedDevice!!)
            val pairs =
                    romCollectionPanel
                            .listSelectedRomIds()
                            .mapNotNull { romId -> deviceSyncStatus.findLocalRom(romId) }
                            .flatMap { localRom ->
                                fileSelector(localRom).map { file ->
                                    val consoleFolder = client.romConsoleFolder(localRom.console())
                                    val filePath = consoleFolder + file.name
                                    file to filePath
                                }
                            }

            client.uploadFilesToDevice(pairs)
            renderDeviceSyncStatus()
        }
    }

}
