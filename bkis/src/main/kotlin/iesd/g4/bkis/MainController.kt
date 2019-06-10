package iesd.g4.bkis

import iesd.g4.bkis.Models.InputModels.DirectDebitIM
import iesd.g4.bkis.Models.InputModels.TransferIM
import iesd.g4.bkis.Models.Transfer
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


@RestController
@RequestMapping("/v1/")
class MainController {
    var transferCounter = 0

    @Value("\${quorum}")
    private val quorum: Array<String>? = null

    @PostMapping("/transfer")
    @ResponseBody
    fun createTransfer(@RequestBody transfer: TransferIM): ResponseEntity<Any> {
        if(quorum != null && SendToQuorum(Transfer(transferCounter++, transfer.orig_account, transfer.dest_account, transfer.value))){
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.status(500).body("Couldn't find quorum")
    }

    @PostMapping("/direct_debit")
    @ResponseBody
    fun createDirectDebit(@RequestBody dd: DirectDebitIM): ResponseEntity<Any> {
        if(quorum != null && SendToQuorum(Transfer(transferCounter++, dd.client_account, dd.dest_account, dd.value))){
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.status(500).body("Couldn't find quorum")
    }

    fun SendToQuorum(transfer: Transfer): Boolean{
        var listHttpStatus: MutableList<HttpStatus> = mutableListOf()
        if(quorum != null){
            for( url in quorum){
                try{
                    var restTemplate = RestTemplate()
                    var request = HttpEntity<Transfer>(transfer);
                    var response = restTemplate.exchange(url + "/transfer", HttpMethod.POST, request, Transfer::class.java)
                    listHttpStatus.add(response.statusCode)
                }
                catch(e: Exception){
                    println("Exception" + e.message)
                    return false
                }
            }
            var okList = listHttpStatus.filter { it == HttpStatus.OK }
            return okList.size >= quorum.size/2
        }
        return false
    }
}