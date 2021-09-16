package be.encelade.vaporwave.gui.comparators

import be.encelade.vaporwave.gui.RomRow

internal class NbrOfRomFilesComparator : Comparator<RomRow> {

    override fun compare(row1: RomRow?, row2: RomRow?): Int {
        return if (row1 != null && row2 != null) {
            row1.nbrOfRomFiles().compareTo(row2.nbrOfRomFiles())
        } else {
            0
        }
    }

}
