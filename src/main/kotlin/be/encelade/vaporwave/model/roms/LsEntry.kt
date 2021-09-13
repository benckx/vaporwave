package be.encelade.vaporwave.model.roms

import be.encelade.vaporwave.services.ExtensionMap.consoleKeys
import org.joda.time.DateTime

data class LsEntry(val lastModified: DateTime,
                   val fileSize: Long,
                   val filePath: String) {

    fun isConsole(): Boolean {
        return console() != null
    }

    fun console(): String? {
        return consoleKeys.find { console -> filePath.startsWith("/roms/$console/") }
    }

    /**
     * @return filePath without extension or folder
     */
    fun simpleFileName(): String {
        val extension = filePath.split(".").last()
        return filePath.removeSuffix(".$extension").split("/").last()
    }

    fun fileName(): String {
        return filePath.split("/").last()
    }

    fun extension(): String {
        return filePath.split(".").last()
    }

}
