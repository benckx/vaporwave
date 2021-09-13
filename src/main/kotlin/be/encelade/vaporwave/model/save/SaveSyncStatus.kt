package be.encelade.vaporwave.model.save

enum class SaveSyncStatus {

    SAVE_STATUS_UNKNOWN,
    NO_SAVE_FOUND,
    SAVE_SYNCED,
    SAVE_ONLY_ON_DEVICE,
    SAVE_MORE_RECENT_ON_DEVICE,
    SAVE_ONLY_ON_COMPUTER,
    SAVE_MORE_RECENT_ON_COMPUTER;

    fun lowerCase(): String {
        return name
                .removePrefix("SAVE_")
                .replace("_", " ")
                .trim()
                .lowercase()
    }

}
