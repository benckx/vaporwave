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

    val syncDiff = localRomManager.calculateSyncDiff(localRoms, remoteRoms)
    println("synced -> ${syncDiff.synced.size}")
    println("not on device -> ${syncDiff.notOnDevice.size}")
    println("not on local folder -> ${syncDiff.notInLocalFolder.size}")

    println()
    println("[NOT ON DEVICE]")
    syncDiff.notOnDevice.forEach { println(it) }

    println()
    println("[NOT ON LOCAL]")
    syncDiff.notInLocalFolder.forEach { println(it) }

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    val gui = MainGui()
//    gui.renderLocalRoms(localRoms)
    gui.renderAllRoms(localRoms, remoteRoms, syncDiff)
    gui.isVisible = true
}
