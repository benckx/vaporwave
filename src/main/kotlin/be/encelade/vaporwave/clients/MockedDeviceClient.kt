package be.encelade.vaporwave.clients

import org.apache.commons.io.FileUtils.readFileToString
import java.io.File

class MockedDeviceClient : DeviceClient {

    override fun isReachable() = true

    override fun listRomFolderFiles(): String {
        return readFileToString(File("data/ls-result-test-01"), "UTF-8")
    }

}
