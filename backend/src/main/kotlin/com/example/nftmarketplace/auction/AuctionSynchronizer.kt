package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.auction.storage.db.AuctionRepository
import com.example.nftmarketplace.core.auction.AuctionEvents
import com.example.nftmarketplace.nft.requests.CreateNFTRequestHandler
import com.example.nftmarketplace.nft.storage.db.NFTId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.web3j.utils.Convert

@Component
class AuctionSynchronizer(
    @Autowired private val contractHelper: ContractHelper,
    @Autowired private val auctionRepository: AuctionRepository,
    @Autowired private val createNFTRequestHandler: CreateNFTRequestHandler
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    suspend fun synchronizeWithContract() {
        contractHelper.getAllAuctions(1, Int.MAX_VALUE, null).onEach {
            if (!auctionRepository.existsById(it.auctionId).awaitSingle()) {
                onAuctionNotFoundInDatabase(it)
            } else {
                runCatching {
                    createNFTRequestHandler.handle(
                        it.nft.contractAddress,
                        it.nft.tokenId
                    )
                }.onFailure {
                    println(it)
                }
                println("Auction with id ${it.auctionId} already exists in database")
            }
        }.launchIn(coroutineScope)
    }

    suspend fun startSynchronizing() {
        contractHelper.getAuctionsEvents().onEach { event: AuctionEvents ->
            println("Event: $event")
            when (event) {
                is AuctionEvents.Created -> {
                    if (!auctionRepository.existsById(event.id).awaitSingle()) {
                        onAuctionNotFoundInDatabase(contractHelper.getAuctionById(event.id))
                    }
                }
                is AuctionEvents.BidPlaced -> {
                    getAuctionByIdFromDatabase(event.id)?.let {
                        auctionRepository.save(
                            it.copy(
                                currentBid = AuctionEntity.Bid(
                                    bidder = event.bidderAddress,
                                    amount = Convert.fromWei(event.amount.toBigDecimal(), Convert.Unit.ETHER),
                                    timestamp = event.timestamp
                                )
                            )
                        ).awaitSingle()
                    }
                }
                is AuctionEvents.Ended -> {
                    getAuctionByIdFromDatabase(event.id)?.let {
                        auctionRepository.save(
                            it.copy(
                                status = if (event.withWinner) AuctionEntity.Status.Expired else AuctionEntity.Status.Cancelled
                            )
                        ).awaitSingle()
                    }
                }
                is AuctionEvents.Cancelled -> {
                    getAuctionByIdFromDatabase(event.id)?.let {
                        auctionRepository.save(
                            it.copy(
                                status = AuctionEntity.Status.Cancelled
                            )
                        ).awaitSingle()
                    }
                }
                is AuctionEvents.Extended -> {
                    getAuctionByIdFromDatabase(event.id)?.let {
                        auctionRepository.save(
                            it.copy(
                                expiryTime = event.newTime
                            )
                        ).awaitSingle()
                    }
                }
                else -> {}
            }
        }.launchIn(coroutineScope)
    }

    private suspend inline fun getAuctionByIdFromDatabase(auctionId: Long) =
        auctionRepository.findById(auctionId).awaitSingleOrNull()

    private suspend  fun onAuctionNotFoundInDatabase(auction: Auction) {
        runCatching {
            createNFTRequestHandler.handle(
                auction.nft.contractAddress,
                auction.nft.tokenId
            )
        }.onFailure {
            println(it)
        }
        println("Auction not found in database")
        auctionRepository.save(auction.toAuctionEntity()).awaitSingle()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun afterInitialization() {
        coroutineScope.launch {
            synchronizeWithContract()
            startSynchronizing()
        }
    }
}


fun Auction.toAuctionEntity() = AuctionEntity(
    id = auctionId,
    title = title,
    description = description,
    nft = NFTId(
        nft.contractAddress,
        nft.tokenId
    ),
    currentBid = highestBid?.let { bid ->
        AuctionEntity.Bid(
            bidder = bid.bidder,
            amount = bid.amount,
            timestamp = bid.timestamp
        )
    },
    expiryTime = expiryTime,
    status = when (status) {
        Auction.Status.Pending -> AuctionEntity.Status.NotStared
        Auction.Status.Active -> AuctionEntity.Status.Active
        Auction.Status.Cancelled -> AuctionEntity.Status.Cancelled
        Auction.Status.Expired -> AuctionEntity.Status.Expired
        Auction.Status.Won -> AuctionEntity.Status.Won
    },
    startingPrice = startingPrice,
    minimalIncrement = minimumIncrement,
)
