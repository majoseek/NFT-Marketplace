package com.example.nftmarketplace.events.nft

import com.example.nftmarketplace.core.data.DomainEvent

data class NFTTransferredEvent(
    val contractAddress: String,
    val nftId: Long,
    val from: String,
    val to: String,
) : DomainEvent(aggregateId = contractAddress to nftId)
