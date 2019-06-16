package iesd.g4.bkis

import iesd.g4.bkis.Models.DirectDebit
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
        if(quorum != null){
            var resp = sendToQuorum(Transfer(transferCounter++, transfer.orig_account, transfer.dest_account, transfer.value))
            if(resp == HttpStatus.OK){
                return ResponseEntity.ok().build()
            }
            else if(resp == HttpStatus.NOT_FOUND)
            {
                return ResponseEntity.notFound().build()
            }
            else if(resp == HttpStatus.UNAUTHORIZED)
            {
                return ResponseEntity.status(401).body("Not enough money")
            }
        }
        return ResponseEntity.status(500).body("Couldn't find quorum")
    }

    @PostMapping("/direct_debit")
    @ResponseBody
    fun createDirectDebit(@RequestBody dd: DirectDebitIM): ResponseEntity<Any> {
        if(quorum != null){
            var resp = sendToQuorum(DirectDebit(transferCounter++, dd.client_account, dd.dest_account, dd.value))
            if(resp == HttpStatus.OK){
                return ResponseEntity.ok().build()
            }
            else if(resp == HttpStatus.NOT_FOUND)
            {
                return ResponseEntity.notFound().build()
            }
            else if(resp == HttpStatus.UNAUTHORIZED)
            {
                return ResponseEntity.status(401).body("Not enough money")
            }
        }
        return ResponseEntity.status(500).body("Couldn't find quorum")
    }

    fun sendToQuorum(transfer: Transfer): HttpStatus{
        var listHttpStatus: MutableList<HttpStatus> = mutableListOf()
        var response : ResponseEntity<Transfer> = ResponseEntity.ok().build()
        if(quorum != null){
            for( url in quorum){
                try{
                    var restTemplate = RestTemplate()
                    var request = HttpEntity<Transfer>(transfer);
                    response = restTemplate.exchange(url + "/transfer", HttpMethod.POST, request, Transfer::class.java)
                    listHttpStatus.add(response.statusCode)
                }
                catch(e: Exception){
                    println("Exception" + e.message)
                    if(e.message!!.contains("401"))
                        return HttpStatus.UNAUTHORIZED
                    return HttpStatus.INTERNAL_SERVER_ERROR
                }
            }
            var okList = listHttpStatus.filter { it == HttpStatus.OK }
            if(okList.size >= quorum.size/2)
            {
                return response.statusCode
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR
    }
    fun sendToQuorum(transfer: DirectDebit): HttpStatus{
        var listHttpStatus: MutableList<HttpStatus> = mutableListOf()
        var response : ResponseEntity<DirectDebit> = ResponseEntity.ok().build()
        if(quorum != null){
            for( url in quorum){
                try{
                    var restTemplate = RestTemplate()
                    var request = HttpEntity<DirectDebit>(transfer);
                    response = restTemplate.exchange(url + "/direct_debit", HttpMethod.POST, request, DirectDebit::class.java)
                    listHttpStatus.add(response.statusCode)
                }
                catch(e: Exception){
                    println("Exception" + e.message)
                    if(e.message!!.contains("401"))
                        return HttpStatus.UNAUTHORIZED
                    return HttpStatus.INTERNAL_SERVER_ERROR
                }
            }
            var okList = listHttpStatus.filter { it == HttpStatus.OK }
            if(okList.size >= quorum.size/2)
            {
                return response.statusCode
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR
    }
}