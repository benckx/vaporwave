package be.encelade.vaporwave.model.devices

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type")
abstract class Device(val id: String)
