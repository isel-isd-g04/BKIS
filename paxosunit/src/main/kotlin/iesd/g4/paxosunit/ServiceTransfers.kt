package iesd.g4.paxosunit

import iesd.g4.paxosunit.Models.Account
import iesd.g4.paxosunit.Models.Enums.MessageCode
import iesd.g4.paxosunit.Models.InputModels.TransferIM
import iesd.g4.paxosunit.Models.Transfer

class ServiceTransfers{
    companion object {
        @JvmStatic
        fun createTransferAndAlterFunds(transfer: TransferIM): MessageCode {
            var list_transfers = FileAux.readTransfers()
            var transfer_found = list_transfers!!.find{it.transfer_id==transfer.transfer_id}
            if(transfer_found== null){
                var mutableTransfers = list_transfers.toMutableList()
                mutableTransfers.add(Transfer(transfer.transfer_id, transfer.orig_account, transfer.dest_account, transfer.value))
                return alterFunds(transfer)
            }
            return MessageCode.GENERIC_ERROR
        }

        @JvmStatic
        fun alterFunds(transfer: TransferIM): MessageCode{
            var list_accounts = FileAux.readAccounts()
            var list_accounts_found = list_accounts!!.filter{it.account.equals(transfer.orig_account,true) || it.account.equals(transfer.dest_account,true)}
            var account_found_origin = list_accounts_found!!.find{it.account.equals(transfer.orig_account,true)}
            var account_found_dest = list_accounts_found!!.find{it.account.equals(transfer.dest_account,true)}
            if(account_found_origin!= null && account_found_dest != null){
                if(account_found_origin.value - transfer.value <0){
                    return MessageCode.NOT_ENOUGH_MONEY
                }
                var mutableAccounts = list_accounts.toMutableList()
                mutableAccounts.remove(account_found_origin)
                mutableAccounts.remove(account_found_dest)
                account_found_origin = Account(account_found_origin.client_id, account_found_origin.account,account_found_origin.value - transfer.value)
                mutableAccounts.add(account_found_origin)
                account_found_dest = Account(account_found_dest.client_id, account_found_dest.account,account_found_dest.value + transfer.value)
                mutableAccounts.add(account_found_dest)
                FileAux.writeAccounts(mutableAccounts.toList())
                println("Transfer from account: "+account_found_origin.account+ " to: "+account_found_dest.account+", value: " + transfer.value)
                println("origin current value: "+ account_found_origin.value+ ", destination current value: " + account_found_dest.value)
                return MessageCode.OK
            }
            return MessageCode.ACCOUNT_NOT_FOUND
        }
    }
}