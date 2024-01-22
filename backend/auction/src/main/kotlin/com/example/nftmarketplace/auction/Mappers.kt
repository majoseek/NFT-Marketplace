package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.requests.commands.CreateAuctionCommand
import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.nftauction.NFTAuction
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.web3j.tuples.generated.Tuple12
import org.web3j.utils.Convert
import java.math.BigInteger

typealias NFTAuctionTuple = Tuple12<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String, NFTAuction.Bid, String, String>

fun NFTAuctionTuple.toAuction(bids: List<Auction.Bid>? = null): Auction {
    return Auction(
        auctionId = component1().toLong(),
        title = component11(),
        description = component12(),
        nft = Auction.NFT(
            contractAddress = component8(),
            tokenId = component2().toLong(),
        ),
        startingPrice = Convert.fromWei(
            component5().toBigDecimal(),
            Convert.Unit.ETHER
        ),
        reservePrice = Convert.fromWei(
            component6().toBigDecimal(),
            Convert.Unit.ETHER
        ),
        minimumIncrement = Convert.fromWei(
            component7().toBigDecimal(),
            Convert.Unit.ETHER
        ),
        expiryTime = component3().toLong().let {
            Instant.fromEpochSeconds(it).toLocalDateTime(timeZone = kotlinx.datetime.TimeZone.UTC)
        },
        status = component4().toStatus(),
        bids = bids?.sortedByDescending { it.timestamp }.orEmpty().toMutableList(),
        ownerAddress = component9(),
    )
}

fun BigInteger.toStatus() =
    when (this.toInt()) {
        0 -> Auction.Status.Active
        1 -> Auction.Status.Won
        2 -> Auction.Status.Expired
        else -> Auction.Status.Canceled
    }

fun Auction.Status.toBigInteger(): BigInteger = BigInteger.valueOf(
    when (this) {
        Auction.Status.Active -> 0
        Auction.Status.Won -> 1
        Auction.Status.Expired -> 2
        Auction.Status.Canceled -> 3
    }
)

fun BigInteger.toLocalDateTime() = Instant.fromEpochSeconds(this.toLong())
    .toLocalDateTime(timeZone = TimeZone.UTC)


fun CreateAuctionCommand.Status.toAuctionStatus() =
    when (this) {
        CreateAuctionCommand.Status.Active -> Auction.Status.Active
        CreateAuctionCommand.Status.Won -> Auction.Status.Won
        CreateAuctionCommand.Status.Expired -> Auction.Status.Expired
        CreateAuctionCommand.Status.Canceled -> Auction.Status.Canceled
    }

fun Auction.toAuctionEntity() = AuctionEntity(
    id = auctionId,
    title = title,
    description = description,
    nft = AuctionEntity.NFTId(
        nft.contractAddress,
        nft.tokenId
    ),
    bids = bids.map { AuctionEntity.Bid(it.bidder, it.amount, it.timestamp) },
    expiryTime = expiryTime,
    status = when (status) {
        Auction.Status.Active -> AuctionEntity.Status.Active
        Auction.Status.Won -> AuctionEntity.Status.Won
        Auction.Status.Expired -> AuctionEntity.Status.Expired
        Auction.Status.Canceled -> AuctionEntity.Status.Canceled
    },
    startingPrice = startingPrice,
    minimalIncrement = minimumIncrement,
    ownerAddress = ownerAddress
)

fun Auction.toCreateAuctionCommand() = CreateAuctionCommand(
    auctionId = auctionId,
    title = title,
    description = description,
    nftContractAddress = nft.contractAddress,
    nftTokenId = nft.tokenId,
    startingPrice = startingPrice,
    reservePrice = reservePrice,
    minimumIncrement = minimumIncrement,
    expiryTime = expiryTime,
    bids = bids.map { CreateAuctionCommand.Bid(it.bidder, it.amount, it.timestamp) },
    status = when (status) {
        Auction.Status.Active -> CreateAuctionCommand.Status.Active
        Auction.Status.Won -> CreateAuctionCommand.Status.Won
        Auction.Status.Expired -> CreateAuctionCommand.Status.Expired
        Auction.Status.Canceled -> CreateAuctionCommand.Status.Canceled
    },
    ownerAddress = ownerAddress
)



