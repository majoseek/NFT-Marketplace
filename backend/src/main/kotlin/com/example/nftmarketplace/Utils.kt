package com.example.nftmarketplace

import com.example.nftmarketplace.auction.AuctionResponse
import com.example.nftmarketplace.auction.BidResponse
import com.example.nftmarketplace.auction.NFTAuctionObject
import com.example.nftmarketplace.nft.NFT
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigInteger

suspend fun <T>getOrPrintError(block: suspend () -> T) = runCatching {
    block()
}.getOrElse {
    it.printStackTrace()
    null
}
fun <T>T.getResponseEntity(statusIfNull: HttpStatus = HttpStatus.NOT_FOUND) =
    this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.status(statusIfNull).build()

fun NFTAuctionObject.toAuctionResponse(nft: NFT) = AuctionResponse(
    auctionID = auctionID,
    title = title,
    description = description,
    nft = nft,
    startingPrice = startingPrice,
    reservePrice = reservePrice,
    minimumIncrement = minimumIncrement,
    expiryTime = expiryTime.toString(),
    bids = bids?.map { it.toBidResponse() },
    status = status,
)


fun BigInteger.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochSeconds(this.toLong()).toLocalDateTime(timeZone = TimeZone.UTC)

fun NFTAuctionObject.Bid.toBidResponse() = BidResponse(
    bidder = bidder,
    amount = amount,
    timestamp = timestamp.toString(),
)
