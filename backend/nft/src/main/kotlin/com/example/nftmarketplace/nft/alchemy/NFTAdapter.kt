package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.alchemy.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.alchemy.alchemy.toNFTResponse
import com.example.nftmarketplace.restapi.nfts.NFTResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NFTAdapter(
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
) : NFTQuery {
    override suspend fun getNFT(contractAddress: String, tokenId: Long, withOwner: Boolean): NFTResponse {
        val nft = alchemyAPIAdapter.getNFT(contractAddress, tokenId.toString(), true)
        return nft.toNFTResponse()
    }

    override suspend fun getOwnedNFTs(ownerAddress: String): List<NFTResponse> {
        return alchemyAPIAdapter.getOwnedNFTs(ownerAddress).map { it.toNFTResponse() }
    }

    override suspend fun getNFTs(contractAddress: String, ownerAddress: String?): List<NFTResponse> {
        return alchemyAPIAdapter.getNFTsByOwner(contractAddress, ownerAddress).map { it.toNFTResponse() }
    }
//
//    override suspend fun getOrCreateNFT(contractAddress: String, tokenId: String): NFTDomainModel =
//        if (repository.existsById(NFTId(contractAddress, tokenId.toLong())).awaitSingle()) {
//            getNFT(contractAddress, tokenId)
//        } else createNFTRequestHandler.handle(CreateNFTRequest(contractAddress, tokenId)).toNFTDomainObject()

}
