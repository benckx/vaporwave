package be.encelade.vaporwave.services

import be.encelade.vaporwave.utils.LazyLogging
import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File

class LsParserTest : LazyLogging {

    @Test
    fun parseEntriesTest01() {
        val result = FileUtils.readFileToString(File("data/ls-result-test-01"), "UTF-8")
        val entries = LSParser.parseLsResult(result)
        val remoteRoms = LSParser.findRemoveRoms(entries)

        // TODO: some asserts
//        entries.forEach { logger.info(it.toString()) }
        remoteRoms.forEach { logger.info(it.toString()) }
    }

//    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//    private fun getStringContent(path: String): String {
//        return LsParserTest::class.java.getResource(path).readText()
//    }

}
