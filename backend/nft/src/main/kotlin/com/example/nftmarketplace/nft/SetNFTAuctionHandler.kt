package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.db.NFTId
import com.example.nftmarketplace.nft.db.NFTRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired

interface SetNFTAuctionHandler {
    suspend fun handle(setNFTAuctionCommand: SetNFTAuctionCommand)
}

data class SetNFTAuctionCommand(
    val contractAddress: String,
    val tokenId: Long,
    val auctionId: Long? = null,
)

class SetNFTAuctionHandlerImpl(
    @Autowired private val nftRepository: NFTRepository,
) : SetNFTAuctionHandler {
    override suspend fun handle(setNFTAuctionCommand: SetNFTAuctionCommand) {
        nftRepository.findById(NFTId(setNFTAuctionCommand.contractAddress, setNFTAuctionCommand.tokenId))
            .awaitSingleOrNull()?.let {
                nftRepository.save(it.copy(auctionId = setNFTAuctionCommand.auctionId))
            } ?: throw NFTNotFoundException(setNFTAuctionCommand.contractAddress, setNFTAuctionCommand.tokenId)
    }
}


class NFTNotFoundException(contractAddress: String, tokenId: Long?) :
    RuntimeException("NFT with contract address $contractAddress${ tokenId?.let { " and tokenId: $tokenId"}.orEmpty() } not found")
