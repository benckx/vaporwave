package be.encelade.vaporwave.model.save

enum class SaveSyncStatus {

    SAVE_SYNCED,
    SAVE_ONLY_ON_COMPUTER,
    SAVE_MORE_RECENT_ON_COMPUTER,
    SAVE_ONLY_ON_DEVICE,
    SAVE_MORE_RECENT_ON_DEVICE,
    NO_SAVE_FOUND,
    SAVE_STATUS_UNKNOWN;

    fun lowerCase(): String {
        return name
                .removePrefix("SAVE_")
                .replace("_", " ")
                .trim()
                .lowercase()
    }

    fun canDownloadFromDevice(): Boolean {
        return this == SAVE_SYNCED ||
                this == SAVE_MORE_RECENT_ON_COMPUTER ||
                this == SAVE_ONLY_ON_DEVICE ||
                this == SAVE_MORE_RECENT_ON_DEVICE
    }

    fun canUploadOnDevice(): Boolean {
        return this == SAVE_SYNCED ||
                this == SAVE_ONLY_ON_COMPUTER ||
                this == SAVE_MORE_RECENT_ON_COMPUTER ||
                this == SAVE_MORE_RECENT_ON_DEVICE
    }

}
