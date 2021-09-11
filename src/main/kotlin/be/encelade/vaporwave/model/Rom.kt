package be.encelade.vaporwave.model

abstract class Rom<T>(val console: String,
                      val simpleFileName: String,
                      val entries: List<T>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rom<*>

        if (console != other.console) return false
        if (simpleFileName != other.simpleFileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = console.hashCode()
        result = 31 * result + simpleFileName.hashCode()
        return result
    }

    override fun toString(): String {
        return "Rom[$console] $simpleFileName (${entries.joinToString(", ")})"
    }

    companion object {

        fun matchesBy(rom: Rom<*>, console: String, simpleFileName: String): Boolean {
            return rom.console == console && rom.simpleFileName == simpleFileName
        }

        fun areEquals(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
            return localRom.console == remoteRom.console &&
                    localRom.simpleFileName == remoteRom.simpleFileName
        }

    }

}
