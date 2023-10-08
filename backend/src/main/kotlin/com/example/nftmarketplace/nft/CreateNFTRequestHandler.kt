package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.alchemy.toNFTEntity
import com.example.nftmarketplace.nft.storage.db.NFTId
import com.example.nftmarketplace.nft.storage.db.NFTRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired

interface CreateNFTRequestHandler {
    suspend fun handle(contractAddress: String, tokenId: Long)
}

class CrateNFTRequestHandlerImpl(
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
    @Autowired private val nftRepository: NFTRepository
) : CreateNFTRequestHandler {
    override suspend fun handle(contractAddress: String, tokenId: Long) {
        if (nftRepository.existsById(NFTId(contractAddress, tokenId)).awaitSingle()) {
            throw NFTAlreadyExistsException(contractAddress, tokenId)
        }
        val nft = alchemyAPIAdapter.getNFT(contractAddress, tokenId.toString(), true)
        nftRepository.save(nft.toNFTEntity()).awaitSingle()
    }
}

class NFTAlreadyExistsException(contractAddress: String, tokenId: Long) :
    RuntimeException("NFT with contract address $contractAddress and tokenId $tokenId already exists")
