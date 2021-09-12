package be.encelade.vaporwave.model.roms.comparators

import be.encelade.vaporwave.model.roms.Rom

object ConsoleAndNameRomComparator : Comparator<Rom<*>> {

    override fun compare(p0: Rom<*>?, p1: Rom<*>?): Int {
        val rom1 = p0!!
        val rom2 = p1!!

        val byConsole = rom1.console.compareTo(rom2.console)
        val bySimpleName = rom1.simpleFileName.compareTo(rom2.simpleFileName)

        return if (byConsole == 0) {
            bySimpleName
        } else {
            byConsole
        }
    }

}
