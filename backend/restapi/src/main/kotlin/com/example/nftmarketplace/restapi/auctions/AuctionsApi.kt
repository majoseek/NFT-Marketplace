package com.example.nftmarketplace.restapi.auctions

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller("/auctions")
interface AuctionsApi {

    @GetMapping("")
    suspend fun getAllAuctions(
        @RequestParam("page") page: Int = 1,
        @RequestParam("count") count: Int = 20,
        @RequestParam("status") status: String? = null
    ): ResponseEntity<AuctionsPagedResponse>

    @GetMapping("/{auctionId}")
    suspend fun getAuction(
        @PathVariable("auctionId") auctionId: Long
    ): ResponseEntity<AuctionResponse>

    @GetMapping("/contract/{contractAddress}")
    suspend fun getAuctionsByNFT(
        @PathVariable("contractAddress") contractAddress: String,
        @RequestParam("tokenId") tokenId: Long?
    ): ResponseEntity<Any>


    @GetMapping("/owner/{ownerAddress}")
    suspend fun getAuctionsByOwner(
        @PathVariable("ownerAddress") ownerAddress: String,
        @RequestParam("status") status: String? = null,
    ): ResponseEntity<AuctionsPagedResponse>
}
