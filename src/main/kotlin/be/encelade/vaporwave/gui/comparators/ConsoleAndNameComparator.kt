package be.encelade.vaporwave.gui.comparators

import be.encelade.vaporwave.gui.RomRow

internal class ConsoleAndNameComparator : Comparator<RomRow> {

    override fun compare(row1: RomRow?, row2: RomRow?): Int {
        return if (row1 != null && row2 != null) {
            val byConsole = row1.console().compareTo(row2.console())
            val bySimpleName = row1.simpleFileName().compareTo(row2.simpleFileName())

            return if (byConsole == 0) {
                bySimpleName
            } else {
                byConsole
            }
        } else {
            0
        }
    }

}
