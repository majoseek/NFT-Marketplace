package com.example.nftmarketplace.core.auction

import kotlinx.datetime.LocalDateTime
import java.math.BigInteger

sealed class AuctionEvents {

    abstract val timestamp: LocalDateTime
    abstract val id: Long

    data class Created(override val id: Long, override val timestamp: LocalDateTime) : AuctionEvents()

    data class BidPlaced(override val id: Long, override val timestamp: LocalDateTime, val amount: BigInteger, val bidderAddress: String) : AuctionEvents()

    data class Ended(override val id: Long, override val timestamp: LocalDateTime, val withWinner: Boolean) : AuctionEvents()

    data class Cancelled(override val id: Long, override val timestamp: LocalDateTime) : AuctionEvents()

    data class Extended(override val id: Long, override val timestamp: LocalDateTime, val newTime: LocalDateTime) : AuctionEvents()

    object Unknown : AuctionEvents() {
        override val timestamp: LocalDateTime
            get() = throw IllegalStateException("Unknown event")
        override val id: Long
            get() = throw IllegalStateException("Unknown event")
    }
}

