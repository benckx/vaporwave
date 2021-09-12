import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.LSParser
import org.apache.commons.io.FileUtils
import java.io.File

object TestUtils {

    fun readAsRemoteRoms(filePath: String): List<RemoteRom> {
        val result = FileUtils.readFileToString(File(filePath), "UTF-8")
        val entries = LSParser.parseLsResult(result)
        return LSParser.findRemoveRoms(entries)
    }

}
