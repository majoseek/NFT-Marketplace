package com.example.nftmarketplace.auction

import com.example.nftmarketplace.restapi.auctions.BidElement
import com.example.nftmarketplace.restapi.auctions.SSEAuctionsAPI
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SSEAuctionController(
    private val apiAdapter: AuctionQuery
) : SSEAuctionsAPI {

    @GetMapping("/auction/{auctionId}/bids", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    override suspend fun subscribeToBids(@PathVariable("auctionId") auctionId: Long): Flow<List<BidElement>> {
        return apiAdapter.getAuctionsBids(auctionId)
    }
}
