package be.encelade.vaporwave.model

data class RomSyncStatus(val synced: List<LocalRom>,
                         val notOnDevice: List<LocalRom>,
                         val notOnLocalFolder: List<RemoteRom>)
