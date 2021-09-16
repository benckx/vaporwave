package be.encelade.vaporwave.gui.comparators

import be.encelade.vaporwave.gui.RomRow

internal class LastUpdatedSaveFileComparator : Comparator<RomRow> {

    override fun compare(row1: RomRow?, row2: RomRow?): Int {
        return if (row1 != null && row2 != null) {
            val lastUpdated1 = row1.saveLastModified()
            val lastUpdated2 = row2.saveLastModified()

            if (lastUpdated1 == null && lastUpdated2 != null) {
                -1
            } else if (lastUpdated1 != null && lastUpdated2 == null) {
                1
            } else if (lastUpdated1 != null && lastUpdated2 != null) {
                lastUpdated1.compareTo(lastUpdated2)
            } else {
                0
            }
        } else {
            0
        }
    }

}
