package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.nftauction.NFTAuction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.future.await
import kotlinx.coroutines.plus
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.abi.EventEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.response.BaseEventResponse
import org.web3j.protocol.core.methods.response.EthLog
import org.web3j.protocol.core.methods.response.Log
import org.web3j.tuples.generated.Tuple12
import org.web3j.utils.Convert
import java.math.BigInteger

// TODO this needs refactor
@Component
class ContractHelper(
    @Autowired private val contract: NFTAuction,
    @Autowired private val web3j: Web3j,
) {
    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    suspend fun getAllAuctions(): Flow<Auction> {
        val totalCount = getTotalAuctions()
        return flow {
            val auctions = (1..totalCount).map { auctionId ->
                with(scope) {
                    async { contract.auctions(auctionId.toBigInteger()).sendAsync().get() }
                }
            }

            auctions.awaitAll().forEach { auction ->
                val bids = getAuctionBids(auction.component1().toLong())
                emit(auction.toAuction(bids))
            }
        }
    }

    suspend fun getAuctionById(auctionId: Long): Auction? {
        val maxId = getTotalAuctions()
        require(auctionId in 0..maxId)
        val auction: NFTAuctionTuple? = contract.auctions(BigInteger.valueOf(auctionId)).sendAsync().await()
        return auction?.toAuction(getAuctionBids(auctionId))
    }

    suspend fun getTotalAuctions(): Long {
        return contract.auctionCount().sendAsync().await().toLong()
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
                is NFTAuction.AuctionCanceledEventResponse -> AuctionEvents.Cancelled(
                    event.auctionId.toLong(),
                    timestamp
                )

                is NFTAuction.AuctionCreatedEventResponse -> AuctionEvents.Created(event.auctionId.toLong(), timestamp)
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

    suspend fun getAuctionBids(auctionId: Long): List<Auction.Bid> {
        val ethFilter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            contract.contractAddress
        )

        val eventEncoder = EventEncoder.encode(NFTAuction.BIDPLACED_EVENT)
        ethFilter.addSingleTopic(eventEncoder)


        return web3j.ethGetLogs(ethFilter).sendAsync().await().logs.map {
            val log = it.get() as EthLog.LogObject

            val timestamp = web3j.ethGetBlockByHash(log.blockHash, false)
                .flowable()
                .awaitSingle()
                .block.timestamp.toLocalDateTime()
            (log.tryConvertToEvent() as? NFTAuction.BidPlacedEventResponse)?.let {
                Auction.Bid(
                    bidder = it.bidder,
                    amount = Convert.fromWei(
                        it.amount.toBigDecimal(),
                        Convert.Unit.ETHER
                    ),
                    timestamp = timestamp
                )
            }
        }.toList().filterNotNull()
    }

    companion object {
        private fun Log?.tryConvertToEvent(): BaseEventResponse? {
            this?.let {
                listOf(
                    NFTAuction.AUCTIONCREATED_EVENT to NFTAuction::getAuctionCreatedEventFromLog,
                    NFTAuction.AUCTIONCANCELED_EVENT to NFTAuction::getAuctionCanceledEventFromLog,
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
}


typealias NFTAuctionTuple = Tuple12<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, NFTAuction.Bid>

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

private fun NFTAuctionTuple.toAuction(bids: List<Auction.Bid>? = null): Auction {
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
        expiryTime = component9().toLong().let {
            Instant.fromEpochSeconds(it).toLocalDateTime(timeZone = kotlinx.datetime.TimeZone.UTC)
        },
        status = component10().toStatus(component10().toLong(), bids?.lastOrNull()?.bidder)
            ?: Auction.Status.Cancelled,
        bids = bids?.sortedByDescending { it.timestamp }.orEmpty().toMutableList(),
    )
}

fun BigInteger.toLocalDateTime() = Instant.fromEpochSeconds(this.toLong())
    .toLocalDateTime(timeZone = TimeZone.UTC)


