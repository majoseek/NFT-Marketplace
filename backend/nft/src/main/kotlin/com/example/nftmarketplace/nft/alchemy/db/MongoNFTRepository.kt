package com.example.nftmarketplace.nft.alchemy.db

import com.example.nftmarketplace.common.EventPublisher
import com.example.nftmarketplace.nft.alchemy.alchemy.toNFTEntity
import com.example.nftmarketplace.nft.alchemy.data.NFT
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface DbNFTRepository {
    suspend fun create(nft: NFT)

    suspend fun get(contractAddress: String, tokenId: Long): NFT?

    suspend fun save(nft: NFT)

    suspend fun saveAll(nfts: List<NFT>)
}

@Component
class MongoNFTRepository(
    @Autowired private val nftRepository: NFTRepository,
    private val eventPublisher: EventPublisher
) : DbNFTRepository {
    override suspend fun create(nft: NFT) {
        val nftEntity = nft.toNFTEntity()
        nftRepository.save(nftEntity).awaitSingleOrNull()?.let {
            nft.getEvents().forEach { eventPublisher.publish(it) }
        } ?: throw RuntimeException()
    }

    override suspend fun get(contractAddress: String, tokenId: Long) =  nftRepository.findById(NFTId(contractAddress, tokenId)).awaitSingleOrNull()?.toNFT()

    override suspend fun save(nft: NFT) {
        nftRepository.save(nft.toNFTEntity()).awaitSingleOrNull()?.let {
            nft.getEvents().forEach(eventPublisher::publish)
        }
    }

    override suspend fun saveAll(nfts: List<NFT>) {
        nftRepository.saveAll(nfts.map { it.toNFTEntity() }).collectList().awaitSingleOrNull()?.let {
            nfts.forEach {
                nft -> nft.getEvents().forEach(eventPublisher::publish)
            }
        } ?: throw RuntimeException()
    }
}

private fun NFTEntity.toNFT() = NFT(
    contractAddress = id.contractAddress,
    tokenId = id.tokenId,
    name = name,
    description = description,
    url = url,
    type = NFT.Type.valueOf(this.type.name),
)

