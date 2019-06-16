package iesd.g4.bkis.Models

data class Transfer (val transfer_id: Int, val orig_account: String, val dest_account: String, val value: Double)