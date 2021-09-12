package be.encelade.vaporwave.model.devices

class MockDevice(name: String, val mockDataFileName: String) : Device(name) {

    override fun toString(): String {
        return "Mock Device [$name]"
    }

}
