package com.example.nftmarketplace.auction

import com.example.nftmarketplace.getResponseEntity
import com.example.nftmarketplace.nft.NFT
import com.example.nftmarketplace.nft.NFTService
import com.example.nftmarketplace.toAuctionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.Query
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auction")
class AuctionController(
    @Autowired private val auctionService: AuctionService,
    @Autowired private val nftService: NFTService
) {
    private suspend fun getNft(contractAddress: String, tokenId: Long): NFT? {
        return nftService.getNFT(contractAddress, tokenId.toString())
    }

    @GetMapping("")
    suspend fun getAllAuctions(
        @RequestParam("page") page: Int? = null,
        @RequestParam("count") count: Int? = null
    ): ResponseEntity<AuctionPagedResponse> = AuctionPagedResponse(
        auctions = auctionService.getAllAuctions(page ?: 1, count ?: 20)?.map { it.toAuctionElement() } ?: emptyList(),
        page = page ?: 1,
        size = count ?: 20,
        count = auctionService.getTotalAuctions() ?: 0
    ).getResponseEntity()

    @GetMapping("/{auctionId}")
    suspend fun getAuctionById(@PathVariable("auctionId") auctionId: Long): ResponseEntity<AuctionResponse> {
        val auction = auctionService.getAuctionById(auctionId)
        return auction?.toAuctionResponse(
            getNft(auction.nft.address, auction.nft.tokenID) ?: NFT(
                contractAddress = auction.nft.address,
                tokenID = auction.nft.tokenID,
                name = null,
                null,
                null,
                "",
                NFT.Type.Other
            )
        ).getResponseEntity()
    }


    @GetMapping("/contract/{contractAddress}")
    suspend fun getAuctionByNFT(
        @PathVariable("contractAddress") contractAddress: String,
        @RequestParam("tokenId") tokenId: Long? = null
    ) = auctionService.getAuctionByNFT(contractAddress, tokenId)?.map { it.toAuctionElement() }.getResponseEntity()

    @GetMapping("/owner/{ownerAddress}")
    suspend fun getAuctionByOwner(@PathVariable("ownerAddress") ownerAddress: String) =
        auctionService.getAuctionByOwner(ownerAddress)?.map { it.toAuctionElement() }.getResponseEntity()
}
