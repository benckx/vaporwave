package be.encelade.vaporwave.model

class RemoteRom(console: String, simpleFileName: String, entries: List<LsEntry>) :
        Rom<LsEntry>(console, simpleFileName, entries) {

    override fun toString(): String {
        return "Remote" + super.toString()
    }

}
