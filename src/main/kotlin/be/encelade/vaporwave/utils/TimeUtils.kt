package be.encelade.vaporwave.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object TimeUtils {

    val commandDateTimeFormat: DateTimeFormatter = DateTimeFormat
            .forPattern("YYYY-MM-dd HH:mm:ss.SSSSSSSSS Z")
            .withZoneUTC()

    fun toLocalDateTime(dateTime: DateTime): LocalDateTime {
        return dateTime.withZone(DateTimeZone.getDefault()).toLocalDateTime()
    }

}
