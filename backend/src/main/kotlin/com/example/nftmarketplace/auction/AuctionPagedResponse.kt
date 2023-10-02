package com.example.nftmarketplace.auction

import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.nft.storage.db.NFTEntity
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.nfts.NFTResponse
import com.example.nftmarketplace.toBidResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.minutes


fun AuctionDomainModel.toAuctionElement(): AuctionsPagedResponse.AuctionElement {
    return AuctionsPagedResponse.AuctionElement(
        auctionID = auctionID,
        title = title,
        description = description,
        nft = NFTResponse(
            contractAddress = nft.address,
            tokenID = nft.tokenID,
            name = nft.name,
            url = nft.url,
            type = nft.type?.toResponseType() ?: NFTResponse.Type.Other,
            description = nft.description,
            ownerAddress = nft.ownerAddress
        ),
        expiryTime = expiryTime.toString(),
        highestBid = highestBid?.toBidResponse(),
        status = toStatusResponse(),
    )
}

fun NFTEntity.Type.toResponseType() = when (this) {
    NFTEntity.Type.Image -> NFTResponse.Type.Image
    NFTEntity.Type.Video -> NFTResponse.Type.Video
    NFTEntity.Type.Audio -> NFTResponse.Type.Audio
    NFTEntity.Type.Text -> NFTResponse.Type.Text
    NFTEntity.Type.Other -> NFTResponse.Type.Other
}

fun AuctionDomainModel.toStatusResponse() =
    when (this.status) {
        AuctionDomainModel.Status.Pending -> null
        AuctionDomainModel.Status.Active -> {
            if (expiryTime.toInstant(TimeZone.UTC).plus(30.minutes) > Clock.System.now()) {
                AuctionStatus.Active
            } else {
                AuctionStatus.Ending
            }
        }

        AuctionDomainModel.Status.Cancelled -> AuctionStatus.Cancelled
        AuctionDomainModel.Status.Expired -> AuctionStatus.Expired
        AuctionDomainModel.Status.Won -> AuctionStatus.Completed
    }

