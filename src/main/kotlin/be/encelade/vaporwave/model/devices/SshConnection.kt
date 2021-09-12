package be.encelade.vaporwave.model.devices

data class SshConnection(val username: String,
                         val password: String,
                         val host: String,
                         val port: Int = 22) {

    override fun toString(): String {
        return "$username@$host:$port"
    }

}
