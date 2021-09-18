package be.encelade.vaporwave.utils

import org.joda.time.DateTime
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.FileTime

object FileUtils {

    fun File.setLastModified(lastModified: DateTime) {
        val fileTime = FileTime.fromMillis(lastModified.millis)
        val path = this.toPath()
        Files.setLastModifiedTime(path, fileTime)

    }

}
