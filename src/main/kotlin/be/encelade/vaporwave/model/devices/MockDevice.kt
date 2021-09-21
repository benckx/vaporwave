package be.encelade.vaporwave.model.devices

class MockDevice(name: String,
                 val mockDataFileName: String,
                 val mockDataMd5FileName: String) : Device(name) {

    override fun toString(): String {
        return "Mock Device [$name]"
    }

}
