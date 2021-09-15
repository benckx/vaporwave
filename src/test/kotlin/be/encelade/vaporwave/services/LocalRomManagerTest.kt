package be.encelade.vaporwave.services

import TestUtils.readAsRemoteRoms
import be.encelade.vaporwave.model.roms.LocalRom
import be.encelade.vaporwave.model.roms.RomId
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class LocalRomManagerTest {

    private val manager = LocalRomManager(File("/home/user/roms"))

    @Test
    fun calculateSyncDiffTest01() {
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val diff = manager.calculateSyncDiff(listOf(), remoteRoms)

        assertEquals(0, diff.synced.size)
        assertEquals(0, diff.notOnDevice.size)
        assertEquals(remoteRoms.size, diff.notInLocalFolder.size)
    }

    @Test
    fun calculateSyncDiffTest02() {
        val files = listOf(File("/home/user/roms/gba/MegaMan & Bass.gba)"))
        val localRom = LocalRom(RomId("gba", "MegaMan & Bass"), files, listOf())
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val diff = manager.calculateSyncDiff(listOf(localRom), remoteRoms)

        assertEquals(1, diff.synced.size)
        assertEquals(localRom, diff.synced.first())
        assertEquals(0, diff.notOnDevice.size)
        assertEquals(remoteRoms.size - 1, diff.notInLocalFolder.size)
    }

    @Test
    fun calculateSyncDiffTest03() {
        val files = listOf(
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA).cue"),
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA) (Track 1).bin"),
                File("/home/user/roms/psx/Castlevania - Symphony of the Night (USA) (Track 2).bin"),
        )

        val localRom = LocalRom(RomId("psx", "Castlevania - Symphony of the Night (USA)"), files, listOf())
        val remoteRoms = readAsRemoteRoms("data/ls-result-test-01")
        val diff = manager.calculateSyncDiff(listOf(localRom), remoteRoms)

        assertEquals(1, diff.synced.size)
        assertEquals(localRom, diff.synced.first())
        assertEquals(0, diff.notOnDevice.size)
        assertEquals(remoteRoms.size - 1, diff.notInLocalFolder.size)
    }

}
