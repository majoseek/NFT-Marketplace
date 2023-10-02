package com.example.nftmarketplace

import com.example.nftmarketplace.auction.toStatusResponse
import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.core.data.NFTDomainModel
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.BidElement
import com.example.nftmarketplace.restapi.nfts.NFTResponse
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

fun AuctionDomainModel.toAuctionResponse(nft: NFTDomainModel) = AuctionResponse(
    auctionID = auctionID,
    title = title,
    description = description,
    nft = NFTResponse(
        contractAddress = nft.contractAddress,
        tokenID = nft.tokenID,
        name = nft.name,
        url = nft.url,
        type = nft.type.toResponseType(),
        description = nft.description,
        ownerAddress = nft.ownerAddress
    ),
    startingPrice = startingPrice,
    minimumIncrement = minimumIncrement,
    expiryTime = expiryTime.toString(),
    bids = bids?.map { it.toBidResponse() }.orEmpty(),
    status = this.toStatusResponse() ?: AuctionStatus.Active,
)

fun NFTDomainModel.Type.toResponseType() = when (this) {
    NFTDomainModel.Type.Image -> NFTResponse.Type.Image
    NFTDomainModel.Type.Video -> NFTResponse.Type.Video
    NFTDomainModel.Type.Audio -> NFTResponse.Type.Audio
    NFTDomainModel.Type.Text -> NFTResponse.Type.Text
    NFTDomainModel.Type.Other -> NFTResponse.Type.Other
}


fun BigInteger.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochSeconds(this.toLong()).toLocalDateTime(timeZone = TimeZone.UTC)

fun AuctionDomainModel.Bid.toBidResponse() = BidElement(
    bidder = bidder,
    amount = amount,
    timestamp = timestamp.toString(),
)
