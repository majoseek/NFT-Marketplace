package com.example.nftmarketplace.restapi.auctions

import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
interface SSEAuctionsAPI {
    @GetMapping("/auction/{auctionId}/bids", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun subscribeToBids(@PathVariable("auctionId") auctionId: Long): Flow<List<BidElement>>
}
