package com.example.nftmarketplace.governance.storage.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("proposal")
class ProposalEntity(
    @Id val id: Long,
    val description: String,

)
