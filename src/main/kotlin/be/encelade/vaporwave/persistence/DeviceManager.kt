package be.encelade.vaporwave.persistence

import be.encelade.vaporwave.model.devices.Device
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.commons.io.FileUtils.readFileToString
import org.apache.commons.io.FileUtils.writeStringToFile
import java.io.File
import java.io.File.separator
import kotlin.text.Charsets.UTF_8

class DeviceManager {

    private val jsonMapper = ObjectMapper()
            .registerModule(KotlinModule() as SimpleModule)
            .configure(INDENT_OUTPUT, true)

    private val listOfDevicesTypeRef = object : TypeReference<List<Device>>() {}

    private fun deviceFile(): File = File("data${separator}devices.json")

    fun saveDevices(devices: List<Device>) {
        val json = jsonMapper.writerFor(listOfDevicesTypeRef).writeValueAsString(devices)
        writeStringToFile(deviceFile(), json, UTF_8)
    }

    fun loadDevices(): List<Device> {
        val json = readFileToString(deviceFile(), UTF_8)
        return jsonMapper.readValue(json, listOfDevicesTypeRef)
    }

}
