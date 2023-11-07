package com.example.nftmarketplace.common.events.auctions

import com.example.nftmarketplace.common.data.DomainEvent
import java.math.BigDecimal

data class BidPlacedEvent(
    val auctionId: Long,
    val bidderAddress: String,
    val amount: BigDecimal,
    val timestamp: java.time.LocalDateTime,
) : DomainEvent(aggregateId = auctionId)
