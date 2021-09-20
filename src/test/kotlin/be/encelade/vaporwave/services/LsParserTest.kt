package be.encelade.vaporwave.services

import TestUtils.readAsRemoteRoms
import be.encelade.vaporwave.utils.LazyLogging
import org.junit.Test
import java.io.File.separator

class LsParserTest : LazyLogging {

    @Test
    fun parseEntriesTest01() {
        val remoteRoms = readAsRemoteRoms("data${separator}ls-result-test-02", "data/md5-test-02")

        // TODO: some asserts
//        entries.forEach { logger.info(it.toString()) }
        remoteRoms.forEach { logger.info(it.toString()) }
    }

}
