package be.encelade.vaporwave.model.roms

enum class RomSyncStatus {

    ROM_SYNCED,
    ROM_ONLY_ON_DEVICE,
    ROM_ONLY_ON_COMPUTER,
    ROM_STATUS_UNKNOWN;

    fun lowerCase(): String {
        return name
                .removePrefix("ROM_")
                .replace("_", " ")
                .trim()
                .lowercase()
    }

    fun canUploadOnDevice(): Boolean {
        return this == ROM_SYNCED || this == ROM_ONLY_ON_COMPUTER
    }

}
