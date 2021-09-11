package be.encelade.vaporwave

import be.encelade.vaporwave.clients.MockedDeviceClient
import be.encelade.vaporwave.services.LocalRomManager

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
    println("not on local folder -> ${syncStatus.notOnLocalFolder.size}")

    println()
    println("[NOT ON DEVICE]")
    syncStatus.notOnDevice.forEach { println(it) }

    println()
    println("[NOT ON LOCAL]")
    syncStatus.notOnLocalFolder.forEach { println(it) }
}
