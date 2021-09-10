package be.encelade.vaporwave.model

data class RemoteRom(val console: String,
                     val simpleFileName: String,
                     val entries: List<LsEntry>)
