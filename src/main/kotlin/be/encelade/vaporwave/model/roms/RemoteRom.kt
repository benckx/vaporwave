package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.model.LsEntry

class RemoteRom(console: String, simpleFileName: String, entries: List<LsEntry>) :
        Rom<LsEntry>(console, simpleFileName, entries) {

    override fun totalSize(): Long {
        return entries.sumOf { entry -> entry.fileSize }
    }

    override fun toString(): String {
        return "Remote" + super.toString()
    }

}
