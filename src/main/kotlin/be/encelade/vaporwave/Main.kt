package be.encelade.vaporwave

import be.encelade.vaporwave.clients.MockedDeviceClient

fun main() {
    val client = MockedDeviceClient()
    client.listRoms().forEach { println(it) }
}
