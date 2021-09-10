package be.encelade.vaporwave.services

import be.encelade.vaporwave.utils.LazyLogging
import org.junit.Test

class LsParserTest : LazyLogging {

    @Test
    fun parseEntriesTest01() {

        val result = getStringContent("/ls-result-01")
        val entries = LSParser().parse(result)
        entries.forEach { logger.info(it.toString()) }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getStringContent(path: String): String {
        return LsParserTest::class.java.getResource(path).readText()
    }

}
