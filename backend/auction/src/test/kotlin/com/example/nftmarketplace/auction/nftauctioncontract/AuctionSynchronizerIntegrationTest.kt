package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.utils.TestCancelAuctionRequestHandler
import com.example.nftmarketplace.auction.utils.TestCompleteAuctionRequestHandler
import com.example.nftmarketplace.auction.utils.TestContractHelperImpl
import com.example.nftmarketplace.auction.utils.TestCreateAuctionRequestHandler
import com.example.nftmarketplace.auction.utils.TestExtendAuctionRequestHandler
import com.example.nftmarketplace.auction.utils.TestPlaceBidRequestHandler
import com.example.nftmarketplace.auction.utils.auctionSynchronizer
import com.example.nftmarketplace.auction.utils.dbAuctionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AuctionSynchronizerIntegrationTest {


    val coroutineScheduler = TestScope()

    companion object {
        val localDateTimeExample = LocalDateTime.parse("2021-01-01T00:00:00")
    }

    var createAuctionRequestHandler = TestCreateAuctionRequestHandler()
    var placeBidRequestHandler = TestPlaceBidRequestHandler()
    var extendAuctionRequestHandler = TestExtendAuctionRequestHandler()
    var completeAuctionRequestHandler = TestCompleteAuctionRequestHandler()
    var cancelAuctionRequestHandler = TestCancelAuctionRequestHandler()

    var dbAuctionRepository = dbAuctionRepository()

    var contractHelper: TestContractHelperImpl = TestContractHelperImpl()
    var auctionSynchronizer: AuctionSynchronizer = auctionSynchronizer(
        contractHelper,
        dbAuctionRepository,
        createAuctionRequestHandler,
        placeBidRequestHandler,
        extendAuctionRequestHandler,
        completeAuctionRequestHandler,
        cancelAuctionRequestHandler,
        coroutineScheduler
    )

    @BeforeEach
    fun checkHandledCommands() {
        assertNull(placeBidRequestHandler.handledCommand)
        assertNull(createAuctionRequestHandler.handledCommand)
        assertNull(extendAuctionRequestHandler.handledCommand)
        assertNull(completeAuctionRequestHandler.handledCommand)
        assertNull(cancelAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testSynchronizeWithContract_NewAuction() = runTest {
        val newAuctionId = 1L
        val auction = createAuction(newAuctionId)

        contractHelper.auctions.add(auction)
        auctionSynchronizer.synchronizeWithContract()

        assert(createAuctionRequestHandler.handledCommand?.auctionId == newAuctionId)
    }

    @Test
    fun testSynchronizeWithContract_ExistingAuction() = runTest {
        val auction = createAuction(1L)
        dbAuctionRepository.auctions.add(auction)
        contractHelper.auctions.add(auction)

        auctionSynchronizer.synchronizeWithContract()


        assertNull(createAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testInitialSynchronize_BidsProcessing() = runTest {
        val auction = createAuction(1L)
        contractHelper.auctions.add(auction)
        auctionSynchronizer.initialSynchronize()

        assertNotNull(placeBidRequestHandler.handledCommand)
    }

    @Test
    fun testStartSynchronizing_AuctionCreatedEvent() = runTest {
        val auctionCreatedEvent = AuctionEvents.Created(
            id = 0, timestamp = localDateTimeExample
        )

        contractHelper.postEvent(auctionCreatedEvent)
        auctionSynchronizer.startSynchronizing()

        assertNotNull(createAuctionRequestHandler.handledCommand)

    }

    @Test
    fun testStartSynchronizing_BidPlacedEvent() = runTest {
        val bidPlacedEvent = AuctionEvents.BidPlaced(
            id = 0, timestamp = localDateTimeExample, amount = BigInteger.ONE, bidderAddress = ""
        )

        contractHelper.postEvent(bidPlacedEvent)
        auctionSynchronizer.startSynchronizing()

        assertNotNull(placeBidRequestHandler.handledCommand)
    }

    @Test
    fun testStartSynchronizing_AuctionEndedEvent() = runTest {
        val auctionEndedEvent = AuctionEvents.Ended(
            id = 0, timestamp = localDateTimeExample, winnerAddress = ""
        )

        contractHelper.postEvent(auctionEndedEvent)
        auctionSynchronizer.startSynchronizing()

        assertNotNull(completeAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testStartSynchronizing_AuctionCanceledEvent() = runTest {
        val auctionCanceledEvent = AuctionEvents.Canceled(
            id = 0, timestamp = localDateTimeExample
        )
        auctionSynchronizer.startSynchronizing()
        contractHelper.postEvent(auctionCanceledEvent)
        assertNotNull(cancelAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testStartSynchronizing_AuctionExtendedEvent() = runTest {
        val auctionExtendedEvent = AuctionEvents.Extended(
            id = 0, timestamp = localDateTimeExample, newTime = localDateTimeExample
        )

        contractHelper.postEvent(auctionExtendedEvent)
        auctionSynchronizer.startSynchronizing()

        assertNotNull(extendAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testStartSynchronizing_UnhandledEvent() = runTest {
        val unhandledEvent = AuctionEvents.Unknown

        contractHelper.postEvent(unhandledEvent)
        auctionSynchronizer.startSynchronizing()

        assertNull(createAuctionRequestHandler.handledCommand)
        assertNull(placeBidRequestHandler.handledCommand)
        assertNull(completeAuctionRequestHandler.handledCommand)
        assertNull(cancelAuctionRequestHandler.handledCommand)
        assertNull(extendAuctionRequestHandler.handledCommand)
    }

    @Test
    fun testAfterInitialization_InitialSynchronizeCalled() = runTest {
        auctionSynchronizer.afterInitialization()

        // Add your verification logic here
    }

    @Test
    fun testAfterInitialization_StartSynchronizingCalled() = runTest {
        auctionSynchronizer.afterInitialization()

        // Add your verification logic here
    }


    private fun createAuction(auctionId: Long) = Auction(
        auctionId = auctionId,
        title = "",
        description = "",
        nft = Auction.NFT(contractAddress = "", tokenId = 0),
        ownerAddress = "",
        startingPrice = null,
        reservePrice = null,
        minimumIncrement = null,
        expiryTime = localDateTimeExample,
        bids = mutableListOf(),
        status = Auction.Status.Active,
    )
}
