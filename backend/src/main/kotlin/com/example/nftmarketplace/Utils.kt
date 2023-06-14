package com.example.nftmarketplace

import com.example.nftmarketplace.auction.AuctionResponse
import com.example.nftmarketplace.auction.NFTAuctionObject
import com.example.nftmarketplace.nft.NFT
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

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
    bids = bids,
    status = status,
)
