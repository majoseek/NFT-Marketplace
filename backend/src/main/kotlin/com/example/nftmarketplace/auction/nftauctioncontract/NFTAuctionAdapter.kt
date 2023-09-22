package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.NFTAuctionObject
import com.example.nftmarketplace.auction.AuctionPort
import com.example.nftmarketplace.auction.NFTToken
import com.example.nftmarketplace.nftauction.NFTAuction
import com.example.nftmarketplace.nftauction.NFTAuction.Bid
import com.example.nftmarketplace.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.plus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.web3j.tuples.generated.Tuple13
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.concurrent.CompletableFuture
import kotlin.math.min

typealias NFTAuctionTuple = Tuple13<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Bid?>

@Suppress("UNCHECKED_CAST")
class NFTAuctionAdapter(@Autowired private val contract: NFTAuction) : AuctionPort {

    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()
    // We would create a database for this adapter which stores events

    override suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: NFTAuctionObject.Status?,
    ): List<NFTAuctionObject> = with(scope) {
        require(page > 0)
        val startIndex = (page - 1) * count
        val totalCount = getTotalAuctions()
        val indexes = status?.toBigInteger()?.let {
            val auctionsIds = contract.getAuctionsByStatus(it).sendAsync().waitAndGet() as? List<BigInteger>
            auctionsIds?.slice(
                startIndex until min(startIndex + page * count, auctionsIds.size)
            )?.map { it.toInt() }.orEmpty()
        } ?: (startIndex until min(startIndex + page * count, totalCount.toInt())).toList()

        return indexes.map {
            async { contract.auctions(BigInteger.valueOf(it.toLong())).sendAsync().get() }
        }.awaitAll().map { it.toNFTAuctionObject() }
    }

    override suspend fun getAuctionById(auctionId: Long): NFTAuctionObject = with(scope) {
        val maxId = getTotalAuctions() - 1
        require(auctionId in 0..maxId)
        val auction = contract.auctions(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        auction.toNFTAuctionObject(bids as? List<Bid>)
    }

    override suspend fun getAuctionsByContract(contractAddress: String): List<NFTAuctionObject> =
        with(scope) {
            (contract.getAuctionsByNFT(contractAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
                val auction = contract.auctions(it).sendAsync().waitAndGet()
                auction.toNFTAuctionObject()
            }.orEmpty()
        }

    override suspend fun getAuctionByOwner(ownerAddress: String): List<NFTAuctionObject> = with(scope) {
        (contract.getAuctionsByUser(ownerAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
            contract.auctions(it).sendAsync().waitAndGet().toNFTAuctionObject()
        }.orEmpty()
    }

    override suspend fun getAuctionByStatus(status: NFTAuctionObject.Status) = with(scope) {
        TODO()
    }

    override suspend fun getTotalAuctions(): Long = with(scope) {
        return contract.auctionCount().sendAsync().waitAndGet().toLong()
    }

    override suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): NFTAuctionObject = with(scope) {
        val auctionId = contract.nftToAuction(contractAddress, BigInteger.valueOf(tokenId)).sendAsync().waitAndGet()
        val auction = contract.auctions(auctionId).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(auctionId).sendAsync().waitAndGet()
        auction.toNFTAuctionObject(bids as? List<Bid>)
    }

    context(CoroutineScope)
    private suspend fun <T> CompletableFuture<T>.waitAndGet(): T {
        return async { this@waitAndGet.get() }.await()
    }
}


private fun BigInteger.toStatus(expiryDate: Long): NFTAuctionObject.Status? = when (this.toInt()) {
    0 -> NFTAuctionObject.Status.Pending
    1 -> if (expiryDate >= Clock.System.now().epochSeconds) NFTAuctionObject.Status.Active else NFTAuctionObject.Status.Expired
    2 -> NFTAuctionObject.Status.Cancelled
    else -> null
}

private fun NFTAuctionTuple.toNFTAuctionObject(bids: List<Bid>? = null): NFTAuctionObject {
    return NFTAuctionObject(
        auctionID = component1().toLong(),
        title = component2(),
        description = component3(),
        nft = NFTToken(
            address = component4(),
            tokenID = component5().toLong()
        ),
        startingPrice = Convert.fromWei(component6().toBigDecimal(), Convert.Unit.ETHER),
        reservePrice = Convert.fromWei(component7().toBigDecimal(), Convert.Unit.ETHER),
        minimumIncrement = Convert.fromWei(component8().toBigDecimal(), Convert.Unit.ETHER),
        expiryTime = component10().toLong().let {
            Instant.fromEpochSeconds(it).toLocalDateTime(timeZone = TimeZone.UTC)
        },
        status = component11().toStatus(component10().toLong()) ?: NFTAuctionObject.Status.Cancelled,
        highestBid = component13()?.let {
            if (it.amount != BigInteger.ZERO) {
                NFTAuctionObject.Bid(
                    bidder = it.bidder,
                    amount = Convert.fromWei(it.amount.toBigDecimal(), Convert.Unit.ETHER),
                    timestamp = it.timestamp.toLocalDateTime(),
                )
            } else null
        },
        bids = bids?.map { bid ->
            NFTAuctionObject.Bid(
                bidder = bid.bidder,
                amount = Convert.fromWei(bid.amount.toBigDecimal(), Convert.Unit.ETHER),
                timestamp = bid.timestamp.toLocalDateTime(),
            )
        }?.sortedByDescending { it.timestamp }
    )
}


fun NFTAuctionObject.Status.toBigInteger() = BigInteger.valueOf(
    when (this) {
        NFTAuctionObject.Status.Pending -> 0
        NFTAuctionObject.Status.Expired,
        NFTAuctionObject.Status.Active -> 1

        NFTAuctionObject.Status.Cancelled -> 2
    }
)
