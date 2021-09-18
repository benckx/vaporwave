package be.encelade.vaporwave.gui.api

interface RightClickMenuCallback {

    fun downloadSelectedRomsFromDevice()

    fun downloadSelectedRomsSaveFilesFromDevice()

    fun uploadSelectedRomsToDevice()

    fun uploadSelectedRomsSaveFilesToDevice()

}
