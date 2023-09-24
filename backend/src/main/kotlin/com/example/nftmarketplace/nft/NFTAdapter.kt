package com.example.nftmarketplace.nft

import com.example.nftmarketplace.core.NFTPort
import com.example.nftmarketplace.core.data.NFTDomainModel
import com.example.nftmarketplace.nft.storage.db.NFTId
import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.storage.db.NFTRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class NFTAdapter(
    @Autowired private val repository: NFTRepository,
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
    @Autowired private val createNFTRequestHandler: CreateNFTRequestHandler,
) : NFTPort {
    override suspend fun getNFT(contractAddress: String, tokenId: String, withOwner: Boolean): NFTDomainModel {
        val nft = repository.findById(NFTId(contractAddress, tokenId.toLong())).awaitSingleOrNull()
        return nft?.toNFTDomainObject() ?: run {
            val alchemyNFT = alchemyAPIAdapter.getNFT(contractAddress, tokenId, withOwner)
            val owner = if (withOwner) alchemyAPIAdapter.getNFTOwner(contractAddress, tokenId) else null
            alchemyNFT.toNFT(owner)
        }
    }

    override suspend fun getOwnedNFTs(ownerAddress: String): List<NFTDomainModel> {
        return alchemyAPIAdapter.getOwnedNFTs(ownerAddress).map { it.toNFT() }
    }

    override suspend fun getNFTs(contractAddress: String, ownerAddress: String?): List<NFTDomainModel> {
        return alchemyAPIAdapter.getNFTs(contractAddress, ownerAddress).map { it.toNFT() }
    }

    override suspend fun getOrCreateNFT(contractAddress: String, tokenId: String): NFTDomainModel =
        if (repository.existsById(NFTId(contractAddress, tokenId.toLong())).awaitSingle()) {
            getNFT(contractAddress, tokenId)
        } else createNFTRequestHandler.handle(CreateNFTRequest(contractAddress, tokenId)).toNFTDomainObject()

}
