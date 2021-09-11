package be.encelade.vaporwave.services

import TestUtils.readAsRemoteRoms
import be.encelade.vaporwave.utils.LazyLogging
import org.junit.Test

class LsParserTest : LazyLogging {

    @Test
    fun parseEntriesTest01() {
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")

        // TODO: some asserts
//        entries.forEach { logger.info(it.toString()) }
        remoteRoms.forEach { logger.info(it.toString()) }
    }

}
