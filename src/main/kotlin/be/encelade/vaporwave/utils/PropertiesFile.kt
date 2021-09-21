package be.encelade.vaporwave.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class PropertiesFile(fileName: String) : LazyLogging {

    private val props = Properties()
    private val file = File(fileName)

    init {
        logger.debug("properties file: ${file.absolutePath}")

        if (!file.exists()) {
            file.createNewFile()
        } else {
            props.load(FileInputStream(file))
            props.forEach { (key, value) -> logger.debug("$key = $value") }
        }
    }

    fun isDefined(key: String): Boolean {
        return props.containsKey(key)
    }

    fun getProperty(key: String): String? {
        return props.getProperty(key)
    }

    fun persistProperty(key: String, value: String) {
        props.setProperty(key, value)
        props.store(FileOutputStream(file), null)
        logger.debug("persisted $key -> $value")
    }

}
