package be.encelade.vaporwave.model.devices

class SshDevice(name: String, val conn: SshConnection) : Device(name) {

    override fun toString(): String {
        return "SSH Device $conn [$name]"
    }

}
