package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.requests.CancelAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CompleteAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CreateAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.ExtendAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.PlaceBidRequestHandler
import com.example.nftmarketplace.auction.requests.commands.CancelAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.CompleteAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.ExtendAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.PlaceBidCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import com.example.nftmarketplace.auction.toCreateAuctionCommand
import com.example.nftmarketplace.common.getLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class AuctionSynchronizer(
    @Autowired private val contractHelper: ContractHelper,
    @Autowired private val auctionRepository: DbAuctionRepository,
    @Autowired private val createAuctionHandler: CreateAuctionRequestHandler,
    @Autowired private val placeBidRequestHandler: PlaceBidRequestHandler,
    @Autowired private val extendAuctionRequestHandler: ExtendAuctionRequestHandler,
    @Autowired private val completeAuctionRequestHandler: CompleteAuctionRequestHandler,
    @Autowired private val cancelAuctionRequestHandler: CancelAuctionRequestHandler,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()
) {


    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    suspend fun synchronizeWithContract() {
        contractHelper.getAllAuctions().onEach { auction ->
            if (auctionRepository.get(auction.auctionId) == null) {
                getLogger().info("Auction with id: ${auction.auctionId} not found in the database. Adding...")
                createAuctionHandler.handle(auction.toCreateAuctionCommand())
            }
        }.launchIn(coroutineScope)
    }

    suspend fun initialSynchronize() {
        val range = 1..contractHelper.getTotalAuctions()
        range.filter {
            auctionRepository.get(it) == null
        }.mapNotNull { id ->
            getLogger().info("Auction with id: $id not found in the database. Adding...")
            contractHelper.getAuctionById(id)
        }.toList().forEach {
            createAuctionHandler.handle(it.toCreateAuctionCommand())
        }
        contractHelper.getBids().map {
            placeBidRequestHandler.handle(PlaceBidCommand(it.auctionId, it.bidderAddress, it.amount, it.timestamp.toKotlinLocalDateTime()))
        }
    }

    suspend fun startSynchronizing() {
        contractHelper.getAuctionsEvents().onEach { event: AuctionEvents ->
            println("Event: $event")
            when (event) {
                is AuctionEvents.Created -> {
                    if (auctionRepository.get(event.id) == null) {
                        contractHelper.getAuctionById(event.id)?.let {
                            createAuctionHandler.handle(it.toCreateAuctionCommand())
                        }
                    }
                }
                is AuctionEvents.BidPlaced -> {
                    try {
                        placeBidRequestHandler.handle(PlaceBidCommand(event.id, event.bidderAddress, event.amount.toBigDecimal(), event.timestamp))
                    } catch(e: Exception) {
                        getLogger().error("Error while placing bid: ${e.message}")
                    }
                }
                is AuctionEvents.Ended -> {
                    completeAuctionRequestHandler.handle(
                        CompleteAuctionCommand(
                            event.id,
                            event.winnerAddress,
                        )
                    )
                }
                is AuctionEvents.Canceled -> {
                    cancelAuctionRequestHandler.handle(CancelAuctionCommand(event.id))
                }
                is AuctionEvents.Extended -> {
                    extendAuctionRequestHandler.handle(ExtendAuctionCommand(event.id, event.newTime))
                }
                else -> {}
            }
        }.launchIn(coroutineScope)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun afterInitialization() {
        getLogger().info("Starting synchronizing with the contract...")
        coroutineScope.launch {
            initialSynchronize()
            startSynchronizing()
        }
    }
}
