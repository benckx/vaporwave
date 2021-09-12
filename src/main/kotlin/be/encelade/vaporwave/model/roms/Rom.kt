package be.encelade.vaporwave.model.roms

abstract class Rom<T>(val console: String,
                      val simpleFileName: String,
                      val romFiles: List<T>) {

    abstract fun romFilesSize(): Long

    fun matchesBy(console: String, simpleFileName: String): Boolean {
        return this.console == console && this.simpleFileName == simpleFileName
    }

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
        return "Rom[$console] $simpleFileName (${romFiles.joinToString(", ")})"
    }

    companion object {

        fun areEquals(localRom: LocalRom, remoteRom: RemoteRom): Boolean {
            return localRom.console == remoteRom.console &&
                    localRom.simpleFileName == remoteRom.simpleFileName
        }

    }

}
