package be.encelade.vaporwave

fun main() {
    val client = Client("ark", "ark", "192.168.178.57", 22)
    if (client.isOnline()) {
//        val command = " ls -l --time-style=full-iso /roms/*/*.{srm,state}"
        val command = " ls -l --time-style=full-iso /roms/*/*.*"
        val result = client.sendCommand(command)
        println(result)
    } else {
        println("offline")
    }
}
