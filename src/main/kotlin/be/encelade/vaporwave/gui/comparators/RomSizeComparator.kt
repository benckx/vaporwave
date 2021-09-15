package be.encelade.vaporwave.gui.comparators

import be.encelade.vaporwave.gui.RomRow

internal class RomSizeComparator : Comparator<RomRow> {

    override fun compare(row1: RomRow?, row2: RomRow?): Int {
        return if (row1 != null && row2 != null) {
            row1.romFilesSize().compareTo(row2.romFilesSize())
        } else {
            0
        }
    }

}
