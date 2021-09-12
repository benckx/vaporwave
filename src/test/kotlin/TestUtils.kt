import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.LSParser
import org.apache.commons.io.FileUtils.readFileToString
import java.io.File
import kotlin.text.Charsets.UTF_8

object TestUtils {

    fun readAsRemoteRoms(filePath: String): List<RemoteRom> {
        val result = readFileToString(File(filePath), UTF_8)
        val entries = LSParser.parseLsResult(result)
        return LSParser.findRemoteRoms(entries)
    }

}
