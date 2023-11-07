package com.example.nftmarketplace.auction

import com.example.nftmarketplace.common.AggregateRoot
import com.example.nftmarketplace.common.events.auctions.AuctionCancelledEvent
import com.example.nftmarketplace.common.events.auctions.AuctionCompletedEvent
import com.example.nftmarketplace.common.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.common.events.auctions.AuctionExtendedEvent
import com.example.nftmarketplace.common.events.auctions.BidPlacedEvent
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.math.BigDecimal

data class Auction(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nft: NFT,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    var expiryTime: LocalDateTime,
    val bids: MutableList<Bid> = mutableListOf(),
    var status: Status,
) : AggregateRoot() {
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )

    enum class Status {
        Active,
        Won,
        Expired,
        Canceled
    }

    data class NFT(
        val contractAddress: String,
        val tokenId: Long
    )

    fun placeBid(
        bidder: String,
        amount: BigDecimal,
        timestamp: LocalDateTime
    ) {
        bids.add(
            Bid(
                bidder = bidder,
                amount = amount,
                timestamp = timestamp
            )
        )
        record(BidPlacedEvent(auctionId, bidder, amount, timestamp.toJavaLocalDateTime()))
    }

    fun extend(newTime: LocalDateTime) {
        expiryTime = newTime
        record(
            AuctionExtendedEvent(
                auctionId = auctionId,
                newExpiryTime = newTime.toJavaLocalDateTime()
            )
        )
    }

    fun completeWithWinner(winner: String) {
        status = Status.Won
        record(
            AuctionCompletedEvent(
                auctionId = auctionId,
                winnerAddress = winner,
                contractAddress = nft.contractAddress,
                tokenId = nft.tokenId
            )
        )
    }

    fun completeAuctionWithoutWinner() {
        status = Status.Expired
        record(
            AuctionCompletedEvent(
                auctionId = auctionId,
                winnerAddress = null,
                contractAddress = nft.contractAddress,
                tokenId = nft.tokenId
            )
        )
    }

    fun cancel() {
        status = Status.Canceled
        record(AuctionCancelledEvent(auctionId))
    }

    companion object {
        fun create(
            auctionId: Long,
            title: String,
            description: String,
            nftContractAddress: String,
            nftTokenId: Long,
            startingPrice: BigDecimal?,
            reservePrice: BigDecimal?,
            minimumIncrement: BigDecimal?,
            expiryTime: LocalDateTime,
            bids: List<Bid> = emptyList(),
            status: Status = Status.Active,
        ) = Auction(
            auctionId = auctionId,
            title = title,
            description = description,
            nft = NFT(nftContractAddress, nftTokenId),
            startingPrice = startingPrice,
            reservePrice = reservePrice,
            minimumIncrement = minimumIncrement,
            expiryTime = expiryTime,
            bids = bids.toMutableList(),
            status = status,
        ).apply {
            record(
                AuctionCreatedEvent(
                    auctionId = auctionId,
                    nftContractAddress = nft.contractAddress,
                    nftTokenId = nft.tokenId,
                    title = title,
                    description = description,
                    expiryTime = expiryTime.toString(),
                    startingPrice = startingPrice.toString(),
                    minimalIncrement = minimumIncrement.toString(),
                    status = when (status) {
                        Status.Active -> AuctionCreatedEvent.Status.Active
                        Status.Won -> AuctionCreatedEvent.Status.Won
                        Status.Expired -> AuctionCreatedEvent.Status.Expired
                        Status.Canceled -> AuctionCreatedEvent.Status.Canceled
                    },
                    bids = bids.map {
                        AuctionCreatedEvent.Bid(
                            bidder = it.bidder,
                            amount = it.amount.toString(),
                            timestamp = it.timestamp.toString()
                        )
                    },

                )
            )
        }
    }
}
