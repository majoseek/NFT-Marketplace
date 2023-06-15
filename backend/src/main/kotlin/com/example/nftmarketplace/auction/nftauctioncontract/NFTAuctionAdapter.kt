package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.NFTAuctionObject
import com.example.nftmarketplace.auction.AuctionPort
import com.example.nftmarketplace.auction.NFTToken
import com.example.nftmarketplace.nftauction.NFTAuction
import com.example.nftmarketplace.nftauction.NFTAuction.Bid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.plus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.web3j.tuples.generated.Tuple13
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

typealias NFTAuctionTuple = Tuple13<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Bid?>

@Suppress("UNCHECKED_CAST")
class NFTAuctionAdapter(@Autowired private val contract: NFTAuction) : AuctionPort {

    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()
    // auctionRecordId uint256, title string, description string, assetAddress address, assetRecordId uint256, startingPrice uint128, reservePrice uint128, minimumIncrement uint128, distributionCut uint8, expiryTime uint256, status uint8, sellerAddress address, highestBid tuple
    // We would create a database for this adapter which stores events

    override suspend fun getAllAuctions(page: Int, count: Int): List<NFTAuctionObject> {
        require(page > 0)
        val startIndex = (page - 1) * count
        val totalCount = getTotalAuctions()
        val list = with(scope) {
            //
            List(
                if (totalCount > startIndex + count) count else (totalCount.toInt() - startIndex).coerceAtLeast(0)
            ) { index ->
                // optimize this
                async {
                    contract.auctions(BigInteger.valueOf(index.toLong() + startIndex)).sendAsync().waitAndGet()
                        .toNFTAuctionObject()
                }
            }
        }
        return list.awaitAll()
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


private fun BigInteger.toStatus(): NFTAuctionObject.Status? = when (this.toInt()) {
    0 -> NFTAuctionObject.Status.Pending
    1 -> NFTAuctionObject.Status.Active
    2 -> NFTAuctionObject.Status.Cancelled
    else -> null
}

private fun NFTAuctionTuple.toNFTAuctionObject(bids: List<Bid>? = null) = NFTAuctionObject(
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
    status = component11().toStatus() ?: com.example.nftmarketplace.auction.NFTAuctionObject.Status.Cancelled,
    highestBid = component13()?.let {
        if (it.amount != BigInteger.ZERO) {
            NFTAuctionObject.Bid(
                bidder = it.bidder,
                amount = Convert.fromWei(it.amount.toBigDecimal(), Convert.Unit.ETHER)
            )
        } else null
    },
    bids = bids?.map { bid ->
        NFTAuctionObject.Bid(
            bidder = bid.bidder,
            amount = Convert.fromWei(bid.amount.toBigDecimal(), Convert.Unit.ETHER)
        )
    }
)
