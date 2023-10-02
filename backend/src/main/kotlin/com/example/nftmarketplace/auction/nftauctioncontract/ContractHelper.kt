package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.NFTToken
import com.example.nftmarketplace.core.auction.AuctionEvents
import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.nftauction.NFTAuction
import com.example.nftmarketplace.toLocalDateTime
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


typealias NFTAuctionTuple = Tuple13<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, NFTAuction.Bid?>

@Component
@Suppress("UNCHECKED_CAST")
class ContractHelper(
    @Autowired private val contract: NFTAuction,
    @Autowired private val web3j: Web3j,
) {

    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()
    // We would create a database for this adapter which stores events

    suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: AuctionDomainModel.Status?,
    ): Flow<AuctionDomainModel> = with(scope) {
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
                emit(
                    async { contract.auctions(BigInteger.valueOf(it.toLong())).sendAsync().get() }.await()
                        .toNFTAuctionObject()
                )
            }
        }
    }

    suspend fun getAuctionById(auctionId: Long): AuctionDomainModel = with(scope) {
        val maxId = getTotalAuctions() - 1
        require(auctionId in 0..maxId)
        val auction = contract.auctions(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(BigInteger.valueOf(auctionId)).sendAsync().waitAndGet()
        auction.toNFTAuctionObject(bids as? List<NFTAuction.Bid>)
    }

    suspend fun getAuctionsByContract(contractAddress: String): List<AuctionDomainModel> =
        with(scope) {
            (contract.getAuctionsByNFT(contractAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
                val auction = contract.auctions(it).sendAsync().waitAndGet()
                auction.toNFTAuctionObject()
            }.orEmpty()
        }

    suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionDomainModel> = with(scope) {
        (contract.getAuctionsByUser(ownerAddress).sendAsync().waitAndGet() as? List<BigInteger>)?.map {
            contract.auctions(it).sendAsync().waitAndGet().toNFTAuctionObject()
        }.orEmpty()
    }

    suspend fun getTotalAuctions(): Long = with(scope) {
        return contract.auctionCount().sendAsync().waitAndGet().toLong()
    }

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionDomainModel = with(scope) {
        val auctionId = contract.nftToAuction(contractAddress, BigInteger.valueOf(tokenId)).sendAsync().waitAndGet()
        val auction = contract.auctions(auctionId).sendAsync().waitAndGet()
        val bids = contract.getBidByAuctionId(auctionId).sendAsync().waitAndGet()
        auction.toNFTAuctionObject(bids as? List<NFTAuction.Bid>)
    }

    fun getAuctionsEvents(): Flow<AuctionEvents> {
        val filter = EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, contract.contractAddress)
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

    context(CoroutineScope)
    private suspend fun <T> CompletableFuture<T>.waitAndGet(): T {
        return async { this@waitAndGet.get() }.await()
    }
}

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


private fun BigInteger.toStatus(expiryDate: Long): AuctionDomainModel.Status? = when (this.toInt()) {
    0 -> AuctionDomainModel.Status.Pending
    1 -> if (expiryDate >= Clock.System.now().epochSeconds) AuctionDomainModel.Status.Active else AuctionDomainModel.Status.Expired
    2 -> AuctionDomainModel.Status.Cancelled
    else -> null
}

private fun NFTAuctionTuple.toNFTAuctionObject(bids: List<NFTAuction.Bid>? = null): AuctionDomainModel {
    return AuctionDomainModel(
        auctionID = component1().toLong(),
        title = component2(),
        description = component3(),
        nft = NFTToken(
            address = component4(),
            tokenID = component5().toLong(),
            null,
            null,
            "",
            "",
            ""
        ),
        startingPrice = Convert.fromWei(component6().toBigDecimal(), Convert.Unit.ETHER),
        reservePrice = Convert.fromWei(component7().toBigDecimal(), Convert.Unit.ETHER),
        minimumIncrement = Convert.fromWei(component8().toBigDecimal(), Convert.Unit.ETHER),
        expiryTime = component10().toLong().let {
            Instant.fromEpochSeconds(it).toLocalDateTime(timeZone = TimeZone.UTC)
        },
        status = component11().toStatus(component10().toLong()) ?: AuctionDomainModel.Status.Cancelled,
        highestBid = component13()?.let {
            if (it.amount != BigInteger.ZERO) {
                AuctionDomainModel.Bid(
                    bidder = it.bidder,
                    amount = Convert.fromWei(it.amount.toBigDecimal(), Convert.Unit.ETHER),
                    timestamp = it.timestamp.toLocalDateTime(),
                )
            } else null
        },
        bids = bids?.map { bid ->
            AuctionDomainModel.Bid(
                bidder = bid.bidder,
                amount = Convert.fromWei(bid.amount.toBigDecimal(), Convert.Unit.ETHER),
                timestamp = bid.timestamp.toLocalDateTime(),
            )
        }?.sortedByDescending { it.timestamp }.orEmpty()
    )
}


fun AuctionDomainModel.Status.toBigInteger() = BigInteger.valueOf(
    when (this) {
        AuctionDomainModel.Status.Pending -> 0
        AuctionDomainModel.Status.Expired,
        AuctionDomainModel.Status.Active -> 1
        AuctionDomainModel.Status.Won,
        AuctionDomainModel.Status.Cancelled -> 2
    }
)
