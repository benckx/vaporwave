package be.encelade.vaporwave.model

import org.apache.commons.text.WordUtils

enum class RomSyncStatus {

    ROM_STATUS_UNKNOWN,
    SYNCED,
    ONLY_ON_DEVICE,
    ONLY_ON_LOCAL;

    fun capitalizedFully(): String {
        return WordUtils.capitalizeFully(name.replace("_", " ").lowercase())
    }

}
