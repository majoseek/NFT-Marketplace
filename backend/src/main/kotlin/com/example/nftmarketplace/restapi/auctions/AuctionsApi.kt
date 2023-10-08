package com.example.nftmarketplace.restapi.auctions

import kotlinx.coroutines.flow.Flow
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam

@Controller("/auctions")
interface AuctionsApi {

    suspend fun getAllAuctions(
        @RequestParam("page") page: Int = 1,
        @RequestParam("count") count: Int = 20,
        @RequestParam("status") status: AuctionStatus? = null
    ): ResponseEntity<AuctionsPagedResponse>

    suspend fun getAuction(
        @RequestParam("auctionID") auctionID: Long
    ): ResponseEntity<AuctionResponse>

    suspend fun getAuctionsByNFT(
        @RequestParam("contractAddress") contractAddress: String,
        @RequestParam("tokenID") tokenID: String
    ): ResponseEntity<AuctionsPagedResponse>

    suspend fun getAuctionsByOwner(
        @RequestParam("ownerAddress") ownerAddress: String
    ): ResponseEntity<AuctionsPagedResponse>

    @MessageMapping("/{auctionId}/bids")
    suspend fun getBids(auctionId: Long): Flow<BidElement>
}
