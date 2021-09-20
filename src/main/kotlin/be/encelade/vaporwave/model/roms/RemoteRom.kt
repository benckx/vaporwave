package be.encelade.vaporwave.model.roms

import org.joda.time.DateTime

class RemoteRom(romId: RomId,
                romFiles: List<LsEntry>,
                saveFiles: List<Pair<LsEntry, String>>) :
        Rom<LsEntry>(romId, romFiles, saveFiles) {

    override fun lastModified(entry: LsEntry): DateTime {
        return entry.lastModified
    }

    override fun romFilesSize(): Long {
        return romFiles.sumOf { entry -> entry.fileSize }
    }

    override fun toString(): String {
        val filesToString = allFiles().map { entry -> entry.filePath }.joinToString(", ")
        return "RemoteRom[${console()}] ${simpleFileName()} ($filesToString)"
    }

}
