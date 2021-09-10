package be.encelade.vaporwave.model

data class LocalRom(val console: String,
                    val simpleFileName: String,
                    val entries: List<LsEntry>)
