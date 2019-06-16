package iesd.g4.paxosunit.Models.InputModels

data class DirectDebitIM (val transfer_id: Int, val client_account: String, val dest_account: String, val value: Double)