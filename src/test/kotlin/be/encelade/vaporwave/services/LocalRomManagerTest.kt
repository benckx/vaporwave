package be.encelade.vaporwave.services

import TestUtils.readAsRemoteRoms
import be.encelade.vaporwave.model.LocalRom
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class LocalRomManagerTest {

    private val manager = LocalRomManager("/home/user/roms")

    @Test
    fun calculateSyncStatusTest01() {
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val status = manager.calculateSyncStatus(listOf(), remoteRoms)

        assertEquals(0, status.synced.size)
        assertEquals(0, status.notOnDevice.size)
        assertEquals(remoteRoms.size, status.notInLocalFolder.size)
    }

    @Test
    fun calculateSyncStatusTest02() {
        val files = listOf(File("/home/user/roms/gba/MegaMan & Bass.gba)"))
        val localRom = LocalRom("gba", "MegaMan & Bass", files)
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val status = manager.calculateSyncStatus(listOf(localRom), remoteRoms)

        assertEquals(1, status.synced.size)
        assertEquals(localRom, status.synced.first())
        assertEquals(0, status.notOnDevice.size)
        assertEquals(remoteRoms.size - 1, status.notInLocalFolder.size)
    }

    @Test
    fun calculateSyncStatusTest03() {
        val files = listOf(
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA).cue"),
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA) (Track 1).bin"),
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA) (Track 2).bin"),
        )

        val localRom = LocalRom("psx", "Castlevania - Symphony of the Night (USA)", files)
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val status = manager.calculateSyncStatus(listOf(localRom), remoteRoms)

        assertEquals(1, status.synced.size)
        assertEquals(localRom, status.synced.first())
        assertEquals(0, status.notOnDevice.size)
        assertEquals(remoteRoms.size - 1, status.notInLocalFolder.size)
    }

}
