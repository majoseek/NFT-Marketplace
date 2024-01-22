package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.toAuction
import com.example.nftmarketplace.auction.toLocalDateTime
import com.example.nftmarketplace.common.events.auctions.BidPlacedEvent
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
import kotlinx.datetime.toJavaLocalDateTime
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

//typealias NFTAuctionTuple = Tuple12<BigInteger, String, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, NFTAuction.Bid>
typealias NFTAuctionTuple = Tuple12<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, String, NFTAuction.Bid, String, String>
interface ContractHelper {
    suspend fun getAllAuctions(): Flow<Auction>
    suspend fun getAuctionById(auctionId: Long): Auction?
    suspend fun getTotalAuctions(): Long
    fun getAuctionsEvents(): Flow<AuctionEvents>
    suspend fun getBids(): List<BidPlacedEvent>
}


@Component
class ContractHelperImpl(
    @Autowired private val contract: NFTAuction,
    @Autowired private val web3j: Web3j,
): ContractHelper {
    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    override suspend fun getAllAuctions(): Flow<Auction> {
        val totalCount = getTotalAuctions()
        return flow {
            val auctions = (1..totalCount).map { auctionId ->
                with(scope) {
                    async { contract.auctions(auctionId.toBigInteger()).sendAsync().get() }
                }
            }

            auctions.awaitAll().forEach { auction ->
                emit(auction.toAuction())
            }
        }
    }

    override suspend fun getAuctionById(auctionId: Long): Auction? {
        val maxId = getTotalAuctions()
        require(auctionId in 0..maxId)
        val auction: NFTAuctionTuple? = contract.auctions(BigInteger.valueOf(auctionId)).sendAsync().await()
        return auction?.toAuction()
    }

    override suspend fun getTotalAuctions(): Long {
        return contract.auctionCount().sendAsync().await().toLong()
    }

    override fun getAuctionsEvents(): Flow<AuctionEvents> {
        val filter =
            EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, contract.contractAddress)
        return web3j.ethLogFlowable(filter).asFlow().map {
            val timestamp = web3j.ethGetBlockByHash(it.blockHash, false)
                .flowable()
                .awaitSingle()
                .block.timestamp.toLocalDateTime()

            when (val event = it.tryConvertToEvent()) {
                is NFTAuction.AuctionCanceledEventResponse -> AuctionEvents.Canceled(
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
                    id = event.auctionId.toLong(),
                    timestamp = timestamp,
                    winnerAddress = getLastBidder(event.auctionId.toLong())
                )

                is NFTAuction.AuctionEndedWithoutWinnerEventResponse -> AuctionEvents.Ended(
                    id = event.auctionId.toLong(),
                    timestamp = timestamp,
                    winnerAddress = null
                )
                else -> AuctionEvents.Unknown
            }
        }.retry()
    }

    private suspend fun getLastBidder(auctionId: Long): String? {
        return contract.auctions(auctionId.toBigInteger()).sendAsync().await().component10().bidder
    }

    override suspend fun getBids(): List<BidPlacedEvent> {
        val ethFilter = EthFilter(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST,
            contract.contractAddress
        )

        val eventEncoder = EventEncoder.encode(NFTAuction.BIDPLACED_EVENT)
        ethFilter.addSingleTopic(eventEncoder)

        return web3j.ethGetLogs(ethFilter).sendAsync().await().logs.mapNotNull {
            val log = it.get() as EthLog.LogObject
            val bidPlacedEvent = log.tryConvertToEvent() as? NFTAuction.BidPlacedEventResponse
            val timestamp = web3j.ethGetBlockByHash(log.blockHash, false)
                .flowable()
                .awaitSingle()
                .block.timestamp.toLocalDateTime()
            bidPlacedEvent?.let {
                BidPlacedEvent(
                    auctionId = bidPlacedEvent.auctionId.toLong(),
                    bidderAddress = bidPlacedEvent.bidder,
                    amount = Convert.fromWei(bidPlacedEvent.amount.toString(), Convert.Unit.ETHER),
                    timestamp = timestamp.toJavaLocalDateTime()
                )
            }
        }
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
