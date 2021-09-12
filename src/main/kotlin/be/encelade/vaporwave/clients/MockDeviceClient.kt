package be.encelade.vaporwave.clients

import be.encelade.vaporwave.model.devices.MockDevice
import org.apache.commons.io.FileUtils.readFileToString
import java.io.File

class MockDeviceClient(device: MockDevice) : DeviceClient<MockDevice>(device) {

    override fun isReachable() = true

    override fun listRomFolderFiles(): String {
        return readFileToString(File("data/${device.mockDataFileName}"), "UTF-8")
    }

}
