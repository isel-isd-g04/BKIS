package iesd.g4.paxosunit

import iesd.g4.paxosunit.Models.Enums.MessageCode
import iesd.g4.paxosunit.Models.InputModels.DirectDebitIM
import iesd.g4.paxosunit.Models.InputModels.TransferIM
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/paxos/")
class MainController {

    @PostMapping("/transfer")
    @ResponseBody
    fun createTransfer(@RequestBody transfer: TransferIM): ResponseEntity<Any> {
        var resp = ServiceTransfers.createTransferAndAlterFunds(transfer)
        if(resp == MessageCode.OK){
            return ResponseEntity.ok().build()
        }
        else if(resp == MessageCode.ACCOUNT_NOT_FOUND){
            return ResponseEntity.status(404).body("Couldn't find one of the accounts")
        }
        else if(resp == MessageCode.NOT_ENOUGH_MONEY){
            return ResponseEntity.status(401).body("Not enough money")
        }
        return ResponseEntity.status(500).build()
    }

    @PostMapping("/direct_debit")
    @ResponseBody
    fun createDirectDebit(@RequestBody debitDirect: DirectDebitIM): ResponseEntity<Any> {
        var resp = ServiceTransfers.createTransferAndAlterFunds(TransferIM(debitDirect.transfer_id, debitDirect.client_account, debitDirect.dest_account, debitDirect.value))
        if(resp == MessageCode.OK){
            return ResponseEntity.ok().build()
        }
        else if(resp == MessageCode.ACCOUNT_NOT_FOUND){
            return ResponseEntity.status(404).body("Couldn't find one of the accounts")
        }
        else if(resp == MessageCode.NOT_ENOUGH_MONEY){
            return ResponseEntity.status(401).body("Not enough money")
        }
        return ResponseEntity.status(500).build()
    }
}