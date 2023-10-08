package com.example.nftmarketplace.auction

import com.example.nftmarketplace.getResponseEntity
import com.example.nftmarketplace.nft.NFTAdapter
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auction")
class AuctionController(
    @Autowired private val auctionService: AuctionAdapter,
    @Autowired private val nftAdapter: NFTAdapter,
) {

    @GetMapping("")
    suspend fun getAllAuctions(
        @RequestParam("page") page: Int? = null,
        @RequestParam("count") count: Int? = null,
        @RequestParam("status") status: AuctionStatus? = null
    ): ResponseEntity<AuctionsPagedResponse> {
        val auctions = auctionService.getAllAuctions(page ?: 1, count ?: 20, status).toList()
        return AuctionsPagedResponse(
            auctions = auctions,
            page = page ?: 1,
            size = auctions.size,
            count = auctionService.getTotalAuctions()
        ).getResponseEntity()
    }

    @GetMapping("/{auctionId}")
    suspend fun getAuctionById(@PathVariable("auctionId") auctionId: Long): ResponseEntity<AuctionResponse> {
        val auction = auctionService.getAuctionById(auctionId)
        return auction.getResponseEntity()
    }


    @GetMapping("/contract/{contractAddress}")
    suspend fun getAuctionByNFT(
        @PathVariable("contractAddress") contractAddress: String,
        @RequestParam("tokenId") tokenId: Long? = null
    ) = (tokenId?.let {
        auctionService.getAuctionByNFT(contractAddress, tokenId)
    } ?: run {
        auctionService.getAuctionsByContract(contractAddress)
    }).getResponseEntity()

    @GetMapping("/owner/{ownerAddress}")
    suspend fun getAuctionByOwner(@PathVariable("ownerAddress") ownerAddress: String) =
        auctionService.getAuctionByOwner(ownerAddress).getResponseEntity()

    @GetMapping("/{auctionId}/bids", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun getBids(@PathVariable("auctionId") auctionId: Long): Flow<Any> {
        val initialFlow = flowOf(auctionService.getAuctionById(auctionId).highestBids?.map {
            BidElement(
                bidder = it.bidder,
                amount = it.amount,
                timestamp = it.timestamp
            )
        }.orEmpty())

        return merge(initialFlow, auctionService.getAuctionsBids(auctionId).map {
            BidElement(
                bidder = it.bidder,
                amount = it.amount,
                timestamp = it.timestamp.toString()
            )
        })
    }
}
