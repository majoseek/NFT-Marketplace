package com.example.nftmarketplace.nft

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/nft")
class NFTController(@Autowired private val nftAdapter: NFTAdapter) {
    @GetMapping("/owner/{ownerAddress}")
    suspend fun getOwnedNFTs(
        @PathVariable("ownerAddress") ownerAddress: String
    ) = nftAdapter.getOwnedNFTs(ownerAddress)?.let { nfts ->
        ResponseEntity.ok(nfts)
    }

    @GetMapping("/contract/{contractAddress}/token/{tokenId}")
    suspend fun getNFTs(
        @PathVariable("contractAddress") contractAddress: String,
        @PathVariable("tokenId") tokenId: Long
    ) = nftAdapter.getNFT(contractAddress, tokenId, true)?.let { nft ->
        ResponseEntity.ok(nft)
    }
}
