package be.encelade.vaporwave.model

abstract class Rom<T>(val console: String,
                      val simpleFileName: String,
                      val entries: List<T>) : Comparable<Rom<T>> {

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

    // TODO: move to 2 comparator
    override fun compareTo(other: Rom<T>): Int {
        val byConsole = this.console.compareTo(other.console)
        val bySimpleName = this.simpleFileName.compareTo(other.simpleFileName)

        return if (byConsole == 0) {
            bySimpleName
        } else {
            byConsole
        }
    }

    override fun toString(): String {
        return "Rom[$console] $simpleFileName (${entries.joinToString(", ")})"
    }

}
