package iesd.g4.paxosunit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import iesd.g4.paxosunit.Models.Account
import iesd.g4.paxosunit.Models.Transfer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import java.io.IOException
import java.io.FileInputStream
import org.springframework.util.ResourceUtils
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Files.readAllBytes

class FileAux{
    companion object {
        val accountsPath = "classpath:accounts.json"
        val transfersPath = "classpath:receivedTransfers.json"

        @JvmStatic
        fun writeFile(filePath: String, content:String){
            try {
                val file = ResourceUtils.getFile(filePath)
                file.writeText(content, Charset.defaultCharset())
            } catch (e: IOException) {
                println("error writing file: "+ filePath)
            }
        }

        @JvmStatic
        fun readFile(filePath: String): String?{
            try {
                val file = ResourceUtils.getFile(filePath)
                val input = FileInputStream(file)
                return String(Files.readAllBytes(file.toPath()))
            } catch (e: IOException) {
                println("error realing file: "+ filePath)
            }
            return null
        }

        @JvmStatic
        fun writeAccounts(list_acc: List<Account>){
            var jsonString = jacksonObjectMapper().writeValueAsString(list_acc)
            if(jsonString!=null)
                writeFile(accountsPath, jsonString)
        }

        @JvmStatic
        fun writeTransfers(list_transfers: List<Transfer>){
            var jsonString = jacksonObjectMapper().writeValueAsString(list_transfers)
            if(jsonString!=null)
                writeFile(transfersPath, jsonString)
        }

        @JvmStatic
        fun readAccounts(): List<Account>?{
            var filecontent = readFile(accountsPath)
            if(filecontent != null ){
                var mapper = jacksonObjectMapper()
                val list_accounts: List<Account> = mapper.readValue(filecontent)
                return list_accounts
            }
            return null
        }

        @JvmStatic
        fun readTransfers(): List<Transfer>?{
            var filecontent = readFile(transfersPath)
            if(filecontent != null ){
                var mapper = jacksonObjectMapper()
                val list_transfers: List<Transfer> = mapper.readValue(filecontent)
                return list_transfers
            }
            return null
        }
    }
}