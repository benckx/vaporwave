import be.encelade.vaporwave.model.roms.RemoteRom
import be.encelade.vaporwave.services.CommandResultParser.lsEntriesToRemoteRoms
import be.encelade.vaporwave.services.CommandResultParser.parseLsResult
import be.encelade.vaporwave.services.CommandResultParser.parseMd5Result
import org.apache.commons.io.FileUtils.readFileToString
import java.io.File
import kotlin.text.Charsets.UTF_8

object TestUtils {

    fun readAsRemoteRoms(lsFilePath: String, md5FilePath: String): List<RemoteRom> {
        val lsResult = readFileToString(File(lsFilePath), UTF_8)
        val md5Result = readFileToString(File(md5FilePath), UTF_8)
        val lsEntries = parseLsResult(lsResult)
        val md5Map = parseMd5Result(md5Result)
        return lsEntriesToRemoteRoms(lsEntries, md5Map)
    }

}
