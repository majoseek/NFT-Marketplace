package com.example.nftmarketplace.projectionservice

import com.example.nftmarketplace.getResponseEntity
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsApi
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auction")
class AuctionController(
    @Autowired private val auctionProjectionQuery: AuctionProjectionQuery
) : AuctionsApi {

    @GetMapping("")
    override suspend fun getAllAuctions(
        @RequestParam("page") page: Int,
        @RequestParam("count") count: Int,
        @RequestParam("status") status: AuctionStatus?
    ): ResponseEntity<AuctionsPagedResponse>{
        val auctions = auctionProjectionQuery.getAllAuctions(page, count, status).toList()
        return AuctionsPagedResponse(
            auctions = auctions,
            page = page,
            size = auctions.size,
            count = auctionProjectionQuery.getTotalAuctions()
        ).getResponseEntity()
    }

    @GetMapping("/{auctionId}")
    override suspend fun getAuction(@PathVariable("auctionId") auctionId: Long): ResponseEntity<AuctionResponse> {
        val auction = auctionProjectionQuery.getAuctionById(auctionId)
        return auction.getResponseEntity()
    }


    @GetMapping("/contract/{contractAddress}")
    override suspend fun getAuctionsByNFT(
        @PathVariable("contractAddress") contractAddress: String,
        @RequestParam("tokenId") tokenId: Long?
    ): ResponseEntity<Any> = (tokenId?.let {
        auctionProjectionQuery.getAuctionByNFT(contractAddress, tokenId)
    } ?: run {
        auctionProjectionQuery.getAuctionsByContract(contractAddress).toList()
    }).getResponseEntity()

    @GetMapping("/owner/{ownerAddress}")
    override suspend fun getAuctionsByOwner(@PathVariable("ownerAddress") ownerAddress: String): ResponseEntity<AuctionsPagedResponse> {
        val auctions = auctionProjectionQuery.getAuctionByOwner(ownerAddress).toList()
        return AuctionsPagedResponse(
            auctions = auctions,
            page = 1,
            size = auctions.size,
            count = auctions.size.toLong()
        ).getResponseEntity()
    }

}
