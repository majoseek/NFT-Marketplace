package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.storage.db.NFTEntity
import com.example.nftmarketplace.nft.storage.db.NFTId
import com.example.nftmarketplace.nft.storage.db.NFTRepository
import com.example.nftmarketplace.nft.storage.db.toNFTEntity
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired

interface CreateNFTRequestHandler {
    suspend fun handle(request: CreateNFTRequest): NFTEntity
}

data class CreateNFTRequest(
    val contractAddress: String,
    val tokenId: String,
)

class CrateNFTRequestHandlerImpl(
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
    @Autowired private val nftRepository: NFTRepository
) : CreateNFTRequestHandler {
    override suspend fun handle(request: CreateNFTRequest): NFTEntity {
        if (nftRepository.existsById(NFTId(request.contractAddress, request.tokenId.toLong())).awaitSingle()) {
            throw Exception("${request.contractAddress}, ${request.tokenId}") // TODO
        }
        val nft = alchemyAPIAdapter.getNFT(request.contractAddress, request.tokenId, true)
        val owner = alchemyAPIAdapter.getNFTOwner(request.contractAddress, request.tokenId)
        return nftRepository.save(nft.toNFTEntity(ownerAddress = owner)).awaitSingle()
    }
}

