package com.example.nftmarketplace.common.events.nft

import com.example.nftmarketplace.common.data.DomainEvent

data class NFTTransferredEvent(
    val contractAddress: String,
    val nftId: Long,
    val from: String,
    val to: String,
) : DomainEvent(aggregateId = contractAddress to nftId)
