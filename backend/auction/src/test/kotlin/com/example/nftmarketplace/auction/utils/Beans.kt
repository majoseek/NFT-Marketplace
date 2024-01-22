package com.example.nftmarketplace.auction.utils

import com.example.nftmarketplace.auction.nftauctioncontract.AuctionSynchronizer
import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.requests.CancelAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CompleteAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CreateAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.ExtendAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.PlaceBidRequestHandler
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import org.springframework.context.annotation.Bean


@Bean
fun contractHelper(): ContractHelper {
    return TestContractHelperImpl()
}

@Bean
fun auctionSynchronizer(
    contractHelper: ContractHelper,
    auctionRepository: DbAuctionRepository,
    createAuctionHandler: CreateAuctionRequestHandler,
    placeBidRequestHandler: PlaceBidRequestHandler,
    extendAuctionRequestHandler: ExtendAuctionRequestHandler,
    completeAuctionRequestHandler: CompleteAuctionRequestHandler,
    cancelAuctionRequestHandler: CancelAuctionRequestHandler,
    coroutineScope: CoroutineScope
): AuctionSynchronizer {
    return AuctionSynchronizer(
        contractHelper,
        auctionRepository,
        createAuctionHandler,
        placeBidRequestHandler,
        extendAuctionRequestHandler,
        completeAuctionRequestHandler,
        cancelAuctionRequestHandler,
        coroutineScope
    )
}

@Bean
fun dbAuctionRepository() = AuctionTestDbRepository()


@Bean
fun coroutineScope() = CoroutineScope(Dispatchers.IO) + SupervisorJob()