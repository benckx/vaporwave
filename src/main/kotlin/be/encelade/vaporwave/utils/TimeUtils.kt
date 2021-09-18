package be.encelade.vaporwave.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime

object TimeUtils {

    fun toLocalDateTime(dateTime: DateTime): LocalDateTime {
        return dateTime.withZone(DateTimeZone.getDefault()).toLocalDateTime()
    }

}
