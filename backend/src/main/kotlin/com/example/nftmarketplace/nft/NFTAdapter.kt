package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.alchemy.toNFTResponse
import com.example.nftmarketplace.nft.storage.db.NFTId
import com.example.nftmarketplace.nft.storage.db.NFTRepository
import com.example.nftmarketplace.restapi.nfts.NFTResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NFTAdapter(
    @Autowired private val repository: NFTRepository,
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
) : NFTQuery {
    override suspend fun getNFT(contractAddress: String, tokenId: Long, withOwner: Boolean): NFTResponse {
        val nft = repository.findById(NFTId(contractAddress, tokenId)).awaitSingleOrNull()
        return nft?.toNFTResponse() ?: throw NFTNotFoundException(contractAddress, tokenId)
    }

    override suspend fun getOwnedNFTs(ownerAddress: String): List<NFTResponse> {
        return alchemyAPIAdapter.getOwnedNFTs(ownerAddress).map { it.toNFTResponse() }
    }

    override suspend fun getNFTs(contractAddress: String, ownerAddress: String?): List<NFTResponse> {
        return alchemyAPIAdapter.getNFTs(contractAddress, ownerAddress).map { it.toNFTResponse() }
    }
//
//    override suspend fun getOrCreateNFT(contractAddress: String, tokenId: String): NFTDomainModel =
//        if (repository.existsById(NFTId(contractAddress, tokenId.toLong())).awaitSingle()) {
//            getNFT(contractAddress, tokenId)
//        } else createNFTRequestHandler.handle(CreateNFTRequest(contractAddress, tokenId)).toNFTDomainObject()

}
