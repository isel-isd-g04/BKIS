package iesd.g4.bkis.Models

data class DirectDebit (val transfer_id: Int, val client_account: String, val dest_account: String, val value: Double)