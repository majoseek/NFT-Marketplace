package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

class NFTTransfered(
    val from: String,
    val to: String,
    val contractAddress: String,
    val tokenId: Long,
    val auctionId: Long,
) : DomainEvent(aggregateId = auctionId)
