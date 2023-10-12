package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

class AuctionWonEvent(
    val auctionId: Long,
    val winnerAddress: String,
    val contractAddress: String,
    val tokenId: Long,
) : DomainEvent(aggregateId = auctionId)
