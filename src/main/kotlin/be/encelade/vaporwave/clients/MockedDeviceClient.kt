package be.encelade.vaporwave.clients

import org.apache.commons.io.FileUtils.readFileToString
import java.io.File

class MockedDeviceClient(private val mockDataFileName: String = "ls-result-test-01") : DeviceClient {

    override fun isReachable() = true

    override fun listRomFolderFiles(): String {
        return readFileToString(File("data/$mockDataFileName"), "UTF-8")
    }

}
