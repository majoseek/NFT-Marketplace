package com.example.nftmarketplace.restapi.nfts

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller("/nft")
interface NFTsApi {

    @GetMapping("/owner/{ownerAddress}")
    suspend fun getOwnedNFTs(
        @PathVariable("ownerAddress") ownerAddress: String
    ): ResponseEntity<List<NFTResponse>>


    @GetMapping("/contract/{contractAddress}/token/{tokenId}")
    suspend fun getNFTs(
        @PathVariable("contractAddress") contractAddress: String,
        @PathVariable("tokenId") tokenId: String
    ): ResponseEntity<NFTResponse>
}
