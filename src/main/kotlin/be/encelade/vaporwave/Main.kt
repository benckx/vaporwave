package be.encelade.vaporwave

import be.encelade.vaporwave.clients.MockedDeviceClient
import be.encelade.vaporwave.gui.MainGui
import be.encelade.vaporwave.services.LocalRomManager
import javax.swing.UIManager

fun main() {
    val localRomManager = LocalRomManager("/home/benoit/roms")
    val localRoms = localRomManager.listLocalRoms()

    val client = MockedDeviceClient()
    val remoteRoms = client.listRoms()

    localRoms.forEach { println(it) }
    remoteRoms.forEach { println(it) }

    val syncStatus = localRomManager.calculateSyncStatus(localRoms, remoteRoms)
    println("synced -> ${syncStatus.synced.size}")
    println("not on device -> ${syncStatus.notOnDevice.size}")
    println("not on local folder -> ${syncStatus.notInLocalFolder.size}")

    println()
    println("[NOT ON DEVICE]")
    syncStatus.notOnDevice.forEach { println(it) }

    println()
    println("[NOT ON LOCAL]")
    syncStatus.notInLocalFolder.forEach { println(it) }

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val gui = MainGui()
    gui.loadLocalRoms(localRoms)
    gui.isVisible = true
}
