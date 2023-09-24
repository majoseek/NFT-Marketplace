package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.auction.storage.db.AuctionRepository
import com.example.nftmarketplace.core.NFTPort
import com.example.nftmarketplace.core.auction.AuctionEvents
import com.example.nftmarketplace.core.data.AuctionDomainModel
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
    @Autowired private val nftPort: NFTPort
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    @Scheduled(fixedDelay = 3 * 60 * 60 * 1000)
    suspend fun synchronizeWithContract() {
        contractHelper.getAllAuctions(1, Int.MAX_VALUE, null).onEach {
            nftPort.getOrCreateNFT(
                contractAddress = it.nft.address,
                tokenId = it.nft.tokenID.toString()
            )
            if (!auctionRepository.existsById(it.auctionID).awaitSingle()) {
                onAuctionNotFoundInDatabase(it)
            } else {
                println("Auction with id ${it.auctionID} already exists in database")
            }
        }.launchIn(coroutineScope)
    }

    suspend fun startSynchronizing() {
        contractHelper.getAuctionsEvents().onEach { event: AuctionEvents ->
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
                                currentBid = AuctionEntity.CurrentBid(
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

    private suspend  fun onAuctionNotFoundInDatabase(auction: AuctionDomainModel) {
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


fun AuctionDomainModel.toAuctionEntity() = AuctionEntity(
    id = auctionID,
    title = title,
    description = description,
    nft = NFTId(
        nft.address,
        nft.tokenID
    ),
    currentBid = highestBid?.let { bid ->
        AuctionEntity.CurrentBid(
            bidder = bid.bidder,
            amount = bid.amount,
            timestamp = bid.timestamp
        )
    },
    expiryTime = expiryTime,
    status = when (status) {
        AuctionDomainModel.Status.Pending -> AuctionEntity.Status.NotStared
        AuctionDomainModel.Status.Active -> AuctionEntity.Status.Active
        AuctionDomainModel.Status.Cancelled -> AuctionEntity.Status.Cancelled
        AuctionDomainModel.Status.Expired -> AuctionEntity.Status.Expired
        AuctionDomainModel.Status.Won -> AuctionEntity.Status.Won
    },
    startingPrice = startingPrice,
    minimalIncrement = minimumIncrement,
)
