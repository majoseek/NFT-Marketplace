package com.example.nftmarketplace.common.events.auctions

import com.example.nftmarketplace.common.data.DomainEvent

data class AuctionExtendedEvent(
    val auctionId: Long,
    val newExpiryTime: java.time.LocalDateTime,
) : DomainEvent(aggregateId = auctionId)
