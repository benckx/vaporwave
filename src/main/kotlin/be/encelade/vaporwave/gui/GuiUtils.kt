package be.encelade.vaporwave.gui

import java.awt.Font
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import javax.swing.BorderFactory
import javax.swing.border.EmptyBorder

object GuiUtils {

    val titleFont: Font = Font("Arial", Font.PLAIN, 19)

    fun createTitleBorder(): EmptyBorder {
        return createEmptyBorder(left = 2, right = 2, bottom = 3, top = 3)
    }

    /**
     * Convenience method with Kotlin named parameters
     */
    fun createEmptyBorder(top: Int = 0, left: Int = 0, bottom: Int = 0, right: Int = 0): EmptyBorder {
        return BorderFactory.createEmptyBorder(top, left, bottom, right) as EmptyBorder
    }

    // https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
    fun humanReadableByteCountBin(bytes: Long): String {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else Math.abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return String.format("%.1f %ciB", value / 1024.0, ci.current())
    }

}
