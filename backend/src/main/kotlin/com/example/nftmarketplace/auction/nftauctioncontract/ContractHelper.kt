package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.toBigInteger
import com.example.nftmarketplace.nftauction.NFTAuction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.plus
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.response.BaseEventResponse
import org.web3j.protocol.core.methods.response.Log
import org.web3j.tuples.generated.Tuple13
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.concurrent.CompletableFuture
import kotlin.math.min

// TODO this needs refactor
@Suppress("UNCHECKED_CAST")
@Component
class ContractHelper(
    @Autowired private val contract: NFTAuction,
    @Autowired private val web3j: Web3j,
) {

    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()
    // We would create a database for this adapter which stores events

    suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: Auction.Status?,
    ): Flow<Auction> = with(scope) {
        require(page > 0)
        val startIndex = (page - 1) * count
        val totalCount = getTotalAuctions()
        val indexes = status?.toBigInteger()?.let {
            val auctionsIds = contract.getAuctionsByStatus(it).sendAsync().waitAndGet() as? List<BigInteger>
            auctionsIds?.slice(
                startIndex until min(startIndex + page * count, auctionsIds.size)
            )?.map { it.toInt() }.orEmpty()
        } ?: (startIndex until min(startIndex + page * count, totalCount.toInt())).toList()

        return flow {
            indexes.map {
                val auction = async {
                    contract.auctions(BigInteger.valueOf(it.toLong())).sendAsync().get()
                }.await()
                val bids = contract.getBidByAuctionId(it.toBigInteger()).sendAsync().waitAndGet() as List<NFTAuction.Bid>
                emit(auction.toAuction(bids))
            }
        }
    }

    suspend fun getAuctionById(auctionId: Long): Auction? = with(scope) {
        val maxId = getTotalAuctions() - 1
        require(auctionId in 0..maxId)
        val auction: NFTAuctionTuple? = contract.auctions(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        auction?.toAuction(bids as? List<NFTAuction.Bid>)
    }

    suspend fun getAuctionsByContract(contractAddress: String): List<Auction> =
        with(scope) {
            (contract.getAuctionsByNFT(contractAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
                val auction = contract.auctions(it).sendAsync().waitAndGet()
                auction.toAuction()
            }.orEmpty()
        }

    suspend fun getAuctionByOwner(ownerAddress: String): List<Auction> = with(scope) {
        (contract.getAuctionsByUser(ownerAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
            contract.auctions(it).sendAsync().waitAndGet().toAuction()
        }.orEmpty()
    }

    suspend fun getTotalAuctions(): Long = with(scope) {
        return contract.auctionCount().sendAsync().waitAndGet().toLong()
    }

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): Auction = with(scope) {
        val auctionId = contract.nftToAuction(contractAddress, BigInteger.valueOf(tokenId)).sendAsync().waitAndGet()
        val auction = contract.auctions(auctionId).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(auctionId).sendAsync().waitAndGet()
        auction.toAuction(bids as? List<NFTAuction.Bid>)
    }

    fun getAuctionsEvents(): Flow<AuctionEvents> {
        val filter =
            EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, contract.contractAddress)
        return web3j.ethLogFlowable(filter).asFlow().map {
            val timestamp = web3j.ethGetBlockByHash(it.blockHash, false)
                .flowable()
                .awaitSingle()
                .block.timestamp.toLocalDateTime()


            when (val event = it.tryConvertToEvent()) {
                is NFTAuction.AuctionCancelledEventResponse -> AuctionEvents.Cancelled(event.id.toLong(), timestamp)
                is NFTAuction.AuctionCreatedEventResponse -> AuctionEvents.Created(event.id.toLong(), timestamp)
                is NFTAuction.BidPlacedEventResponse -> AuctionEvents.BidPlaced(
                    id = event.auctionId.toLong(),
                    amount = event.amount,
                    bidderAddress = event.bidder,
                    timestamp = timestamp
                )

                is NFTAuction.AuctionEndedWithWinnerEventResponse -> AuctionEvents.Ended(
                    event.auctionId.toLong(),
                    timestamp,
                    true
                )

                is NFTAuction.AuctionEndedWithoutWinnerEventResponse -> AuctionEvents.Ended(
                    event.auctionId.toLong(),
                    timestamp,
                    false
                )

                is NFTAuction.AuctionExtendedEventResponse -> AuctionEvents.Extended(
                    event.auctionId.toLong(),
                    timestamp,
                    event.newExpiryTime.toLocalDateTime()
                )

                else -> AuctionEvents.Unknown
            }
        }.retry()
    }

    // TODO Think about this
    suspend fun getAuctionBids(auctionId: Long) = with(scope) {
        val bids = contract.getBidByAuctionId(auctionId.toBigInteger()).sendAsync().waitAndGet() as? List<NFTAuction.Bid>
        bids?.map {
            Auction.Bid(
                bidder = it.bidder,
                amount = Convert.fromWei(
                    it.amount.toBigDecimal(),
                    Convert.Unit.ETHER
                ),
                timestamp = it.timestamp.toLocalDateTime()
            )
        }.orEmpty()
    }

    companion object {
        private fun Log?.tryConvertToEvent(): BaseEventResponse? {
            this?.let {
                listOf(
                    NFTAuction.AUCTIONCREATED_EVENT to NFTAuction::getAuctionCreatedEventFromLog,
                    NFTAuction.AUCTIONCANCELLED_EVENT to NFTAuction::getAuctionCancelledEventFromLog,
                    NFTAuction.BIDPLACED_EVENT to NFTAuction::getBidPlacedEventFromLog,
                    NFTAuction.AUCTIONENDEDWITHWINNER_EVENT to NFTAuction::getAuctionEndedWithWinnerEventFromLog,
                    NFTAuction.AUCTIONENDEDWITHOUTWINNER_EVENT to NFTAuction::getAuctionEndedWithoutWinnerEventFromLog,
                    NFTAuction.AUCTIONEXTENDED_EVENT to NFTAuction::getAuctionExtendedEventFromLog,
                ).forEach { (event, function) ->
                    NFTAuction.staticExtractEventParameters(event, this@tryConvertToEvent)?.let {
                        return function(this)
                    }
                }
            }
            return null
        }
    }

    context(CoroutineScope)
    private suspend fun <T> CompletableFuture<T>.waitAndGet(): T {
        return async { this@waitAndGet.get() }.await()
    }
}


typealias NFTAuctionTuple = Tuple13<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, NFTAuction.Bid?>

private fun BigInteger.toStatus(expiryDate: Long, bidder: String?): Auction.Status? = when (this.toInt()) {
    0 -> Auction.Status.Pending
    1 -> when {
        expiryDate >= Clock.System.now().epochSeconds -> Auction.Status.Active
        bidder != null -> Auction.Status.Won
        else -> Auction.Status.Expired
    }
    2 -> Auction.Status.Cancelled
    else -> null
}

private fun NFTAuctionTuple.toAuction(bids: List<NFTAuction.Bid>? = null): Auction {
    return Auction(
        auctionId = component1().toLong(),
        title = component2(),
        description = component3(),
        nft = Auction.NFT(
            contractAddress = component4(),
            tokenId = component5().toLong(),
        ),
        startingPrice = Convert.fromWei(
            component6().toBigDecimal(),
            Convert.Unit.ETHER
        ),
        reservePrice = Convert.fromWei(component7().toBigDecimal(), Convert.Unit.ETHER),
        minimumIncrement = Convert.fromWei(
            component8().toBigDecimal(),
            Convert.Unit.ETHER
        ),
        expiryTime = component10().toLong().let {
            kotlinx.datetime.Instant.fromEpochSeconds(it).toLocalDateTime(timeZone = kotlinx.datetime.TimeZone.UTC)
        },
        status = component11().toStatus(component10().toLong(), bids?.lastOrNull()?.bidder)
            ?: Auction.Status.Cancelled,
        bids = bids?.map { bid ->
            Auction.Bid(
                bidder = bid.bidder,
                amount = Convert.fromWei(bid.amount.toBigDecimal(), org.web3j.utils.Convert.Unit.ETHER),
                timestamp = bid.timestamp.toLocalDateTime(),
            )
        }?.sortedByDescending { it.timestamp }.orEmpty().toMutableList(),
    )
}

fun BigInteger.toLocalDateTime() = Instant.fromEpochSeconds(this.toLong())
    .toLocalDateTime(timeZone = TimeZone.UTC)


