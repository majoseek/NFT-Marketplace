package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.NFT
import com.example.nftmarketplace.nft.NFTPort
import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFT
import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFTs
import com.example.nftmarketplace.nft.alchemy.data.toNFT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component("AlchemyAPIAdapter")
class AlchemyAPIAdapter(
    @Autowired private val webClient: WebClient
) : NFTPort {
    override suspend fun getNFT(contractAddress: String, tokenId: String): NFT {
        val nft = webClient.get()
            .uri {
                it.path("getNFTMetadata")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("tokenId", tokenId)
                    .queryParam("tokenType", "ERC721")
                    .build()
            }.retrieve().awaitBody<AlchemyNFT>()
        return nft.toNFT()
    }

    override suspend fun getOwnedNFTs(ownerAddress: String): List<NFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("owner", ownerAddress)
                    .build()
            }.retrieve()
            .awaitBody<AlchemyNFTs>()
        return nfts.ownedNfts.map { it.toNFT(ownerAddress) }
    }

    override suspend fun getNFTs(contractAddress: String, ownerAddress: String?): List<NFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("owner", ownerAddress)
                    .queryParam("tokenType", "ERC721")
                    .build()
            }.retrieve()
            .awaitBody<AlchemyNFTs>()
        return nfts.ownedNfts.map { it.toNFT(ownerAddress) }
    }
}
