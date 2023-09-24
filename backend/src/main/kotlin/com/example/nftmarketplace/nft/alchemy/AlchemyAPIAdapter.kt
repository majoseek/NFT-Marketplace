package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFT
import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFTs
import com.example.nftmarketplace.nft.alchemy.data.OwnersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.coroutines.coroutineContext

@Component("AlchemyAPIAdapter")
class AlchemyAPIAdapter(
    @Autowired private val webClient: WebClient
) {
    suspend fun getNFT(contractAddress: String, tokenId: String, withOwner: Boolean): AlchemyNFT {
        with (CoroutineScope(coroutineContext)) {
            val nft = async {
                webClient.get()
                    .uri {
                        it.path("getNFTMetadata")
                            .queryParam("contractAddress", contractAddress)
                            .queryParam("tokenId", tokenId)
                            .queryParam("tokenType", "ERC721")
                            .build()
                    }.retrieve().awaitBody<AlchemyNFT>()
            }
            if (!withOwner) return nft.await()

            val owner = async { getNFTOwner(contractAddress, tokenId) }
            return nft.await()
        }
    }

    suspend fun getOwnedNFTs(ownerAddress: String): List<AlchemyNFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("owner", ownerAddress)
                    .build()
            }.retrieve()
            .awaitBody<AlchemyNFTs>()
        return nfts.ownedNfts
    }

    suspend fun getNFTs(contractAddress: String, ownerAddress: String?): List<AlchemyNFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("owner", ownerAddress)
                    .queryParam("tokenType", "ERC721")
                    .build()
            }.retrieve()
            .awaitBody<AlchemyNFTs>()
        return nfts.ownedNfts
    }

    suspend fun getNFTOwner(contractAddress: String, tokenId: String): String {
        val nft = webClient.get()
            .uri {
                it.path("getOwnersForToken")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("tokenId", tokenId)
                    .build()
            }.retrieve().awaitBody<OwnersResponse>()
        return nft.owners.firstOrNull().orEmpty()
    }
}
