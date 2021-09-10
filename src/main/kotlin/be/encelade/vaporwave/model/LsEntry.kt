package be.encelade.vaporwave.model

import org.joda.time.DateTime

data class LsEntry(val datetime: DateTime,
                   val fileSize: Long,
                   val filePath: String)
