package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.BidElement
import com.example.nftmarketplace.restapi.nfts.NFTResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.math.BigInteger
import kotlin.time.Duration.Companion.minutes

fun Auction.Status.toBigInteger(): BigInteger = BigInteger.valueOf(
    when (this) {
        Auction.Status.Pending -> 0
        Auction.Status.Expired,
        Auction.Status.Active,
        -> 1

        Auction.Status.Won,
        Auction.Status.Cancelled,
        -> 2
    }
)

fun Auction.toAuctionResponse(nft: NFTResponse? = null) = AuctionResponse(
    auctionId = auctionId,
    title = title,
    description = description,
    nft = nft,
    expiryTime = expiryTime.toString(),
    status = status.toAuctionResponseStatus(expiryTime),
    bids = bids.map { bid ->
        BidElement(
            bidder = bid.bidder,
            amount = bid.amount,
            timestamp = bid.timestamp.toString()
        )
    },
    startingPrice = startingPrice,
    minimumIncrement = minimumIncrement,
)

fun Auction.Status.toAuctionResponseStatus(expiryTime: LocalDateTime) = when (this) {
    Auction.Status.Pending -> AuctionStatus.NotStarted
    Auction.Status.Active -> {
        if (expiryTime.toInstant(TimeZone.UTC).plus(30.minutes) < Clock.System.now()) {
            AuctionStatus.Active
        } else {
            AuctionStatus.Ending
        }
    }
    Auction.Status.Cancelled -> AuctionStatus.Cancelled
    Auction.Status.Expired -> AuctionStatus.Expired
    Auction.Status.Won -> AuctionStatus.Completed
}

fun AuctionEntity.Status.toAuctionResponseStatus(expiryTime: LocalDateTime) = when (this) {
    AuctionEntity.Status.NotStared -> AuctionStatus.NotStarted
    AuctionEntity.Status.Active -> {
        if (expiryTime.toInstant(TimeZone.UTC).plus(30.minutes) < Clock.System.now()) {
            AuctionStatus.Active
        } else {
            AuctionStatus.Ending
        }
    }
    AuctionEntity.Status.Cancelled -> AuctionStatus.Cancelled
    AuctionEntity.Status.Expired -> AuctionStatus.Expired
    AuctionEntity.Status.Won -> AuctionStatus.Completed
}




