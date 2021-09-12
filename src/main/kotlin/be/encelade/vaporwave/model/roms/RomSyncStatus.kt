package be.encelade.vaporwave.model.roms

import org.apache.commons.text.WordUtils

enum class RomSyncStatus {

    ROM_STATUS_UNKNOWN,
    SYNCED,
    ONLY_ON_DEVICE,
    ONLY_ON_LOCAL;

    fun capitalizedFully(): String {
        return WordUtils.capitalizeFully(lowerCase())
    }

    fun lowerCase(): String {
        return name.replace("_", " ").lowercase()
    }

}
