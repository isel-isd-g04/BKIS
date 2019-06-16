package iesd.g4.paxosunit.Models.InputModels

data class TransferIM (val transfer_id: Int, val orig_account: String, val dest_account: String, val value: Double)