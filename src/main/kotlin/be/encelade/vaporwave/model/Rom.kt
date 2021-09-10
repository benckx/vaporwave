package be.encelade.vaporwave.model

abstract class Rom<T>(val console: String,
                      val simpleFileName: String,
                      val entries: List<T>) {

    override fun toString(): String {
        return "Rom[$console] $simpleFileName (${entries.joinToString(", ")})"
    }

}
