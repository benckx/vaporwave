package be.encelade.vaporwave

import be.encelade.vaporwave.clients.SshClient

fun main() {
    val client = SshClient("ark", "ark", "192.168.178.57", 22)
    if (client.isReachable()) {
//        val command = " ls -l --time-style=full-iso /roms/*/*.{srm,state}"
        val command = " ls -l --time-style=full-iso /roms/*/*.*"
        val result = client.sendCommand(command)
        println(result)
    } else {
        println("offline")
    }
}
