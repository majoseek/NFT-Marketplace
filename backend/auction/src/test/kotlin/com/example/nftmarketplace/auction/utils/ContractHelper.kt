package com.example.nftmarketplace.auction.utils

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.nftauctioncontract.AuctionEvents
import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.common.events.auctions.BidPlacedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.toJavaLocalDateTime

class TestContractHelperImpl(scope: CoroutineScope) : ContractHelper {

    var auctions = mutableListOf<Auction>()

    private var eventsFlow = MutableSharedFlow<AuctionEvents>()

    suspend fun postEvent(event: AuctionEvents) {
        eventsFlow.emit(event)
    }

    override suspend fun getAllAuctions(): Flow<Auction> = auctions.asFlow()

    override suspend fun getAuctionById(auctionId: Long) = auctions.find { it.auctionId == auctionId }

    override suspend fun getTotalAuctions() = auctions.size.toLong()

    override fun getAuctionsEvents() = eventsFlow.asSharedFlow()

    override suspend fun getBids(): List<BidPlacedEvent> = auctions.map {
        it.bids.map { bid ->
            BidPlacedEvent(
                auctionId = it.auctionId,
                bidderAddress = bid.bidder,
                amount = bid.amount,
                timestamp = bid.timestamp.toJavaLocalDateTime()
            )
        }
    }.flatten()
}
