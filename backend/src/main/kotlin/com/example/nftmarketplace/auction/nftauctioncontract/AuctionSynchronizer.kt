package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.requests.CancelAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CompleteAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CreateAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.ExtendAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.PlaceBidRequestHandler
import com.example.nftmarketplace.auction.requests.commands.CancelAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.CompleteAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.CreateAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.ExtendAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.PlaceBidCommand
import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import com.example.nftmarketplace.getLogger
import com.example.nftmarketplace.nft.storage.db.NFTId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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
    @Autowired private val cancelAuctionRequestHandler: CancelAuctionRequestHandler
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    suspend fun synchronizeWithContract() {
        contractHelper.getAllAuctions(1, Int.MAX_VALUE, null).onEach { auction ->
            if (auctionRepository.get(auction.auctionId) == null) {
                getLogger().info("Auction with id: ${auction.auctionId} not found in the database. Adding...")
                createAuctionHandler.handle(auction.toCreateAuctionCommand())
            }
        }.launchIn(coroutineScope)
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
                    placeBidRequestHandler.handle(PlaceBidCommand(event.id, event.bidderAddress, event.amount.toBigDecimal(), event.timestamp))
                }
                is AuctionEvents.Ended -> {
                    completeAuctionRequestHandler.handle(
                        CompleteAuctionCommand(
                            event.id,
                            "temp".takeIf { event.withWinner }
                        )
                    )
                }
                is AuctionEvents.Cancelled -> {
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
    bids = bids.map { AuctionEntity.Bid(it.bidder, it.amount, it.timestamp) },
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


private fun Auction.toCreateAuctionCommand() = CreateAuctionCommand(
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
    status = CreateAuctionCommand.Status.valueOf(status.name)
)
