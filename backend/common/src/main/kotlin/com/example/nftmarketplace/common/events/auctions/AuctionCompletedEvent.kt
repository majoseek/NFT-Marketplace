package com.example.nftmarketplace.common.events.auctions

import com.example.nftmarketplace.common.data.DomainEvent

class AuctionCompletedEvent(
    val auctionId: Long,
    val winnerAddress: String?,
    val contractAddress: String,
    val tokenId: Long,
) : DomainEvent(aggregateId = auctionId)
