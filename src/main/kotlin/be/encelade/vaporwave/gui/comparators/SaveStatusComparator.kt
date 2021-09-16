package be.encelade.vaporwave.gui.comparators

import be.encelade.vaporwave.gui.RomRow

internal class SaveStatusComparator : Comparator<RomRow> {

    override fun compare(row1: RomRow?, row2: RomRow?): Int {
        return if (row1 != null && row2 != null) {
            row2.saveSyncStatus.ordinal.compareTo(row1.saveSyncStatus.ordinal)
        } else {
            0
        }
    }

}
