package com.example.nftmarketplace.restapi.auctions

import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("/auctions")
interface AuctionsWebsocket {

    @MessageMapping("/{auctionId}/bids")
    suspend fun getBids(auctionId: Long): Flow<BidElement>
}
