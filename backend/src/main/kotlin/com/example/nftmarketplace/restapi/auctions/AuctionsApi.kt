package com.example.nftmarketplace.restapi.auctions

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestParam

@Controller("/auctions")
interface AuctionsApi {

    suspend fun getAllAuctions(
        @RequestParam("page") page: Int? = null,
        @RequestParam("count") count: Int? = null,
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
}
