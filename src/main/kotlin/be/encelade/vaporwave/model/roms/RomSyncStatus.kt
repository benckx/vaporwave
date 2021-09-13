package be.encelade.vaporwave.model.roms

enum class RomSyncStatus {

    ROM_STATUS_UNKNOWN,
    ROM_SYNCED,
    ROM_ONLY_ON_DEVICE,
    ROM_ONLY_ON_COMPUTER;

    fun lowerCase(): String {
        return name
                .removePrefix("ROM_")
                .replace("_", " ")
                .trim()
                .lowercase()
    }

}
