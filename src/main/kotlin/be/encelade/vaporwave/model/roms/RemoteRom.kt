package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.model.LsEntry

class RemoteRom(console: String, simpleFileName: String, romFiles: List<LsEntry>) :
        Rom<LsEntry>(console, simpleFileName, romFiles) {

    override fun romFilesSize(): Long {
        return romFiles.sumOf { entry -> entry.fileSize }
    }

    override fun toString(): String {
        return "Remote" + super.toString()
    }

}
