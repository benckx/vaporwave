package be.encelade.vaporwave

import be.encelade.vaporwave.clients.MockedDeviceClient
import be.encelade.vaporwave.services.LocalRomManager

fun main() {
    val client = MockedDeviceClient()
    client.listRoms().forEach { println(it) }

    val manager = LocalRomManager("/home/benoit/roms")
    manager.listLocalRoms()
}
