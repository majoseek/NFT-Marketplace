package com.example.nftmarketplace.nft.alchemy.alchemy

import com.example.nftmarketplace.nft.alchemy.NFTNotFoundException
import com.example.nftmarketplace.nft.alchemy.alchemy.data.bodyparams.BatchNFTs
import com.example.nftmarketplace.nft.alchemy.alchemy.data.bodyparams.TokenInfo
import com.example.nftmarketplace.nft.alchemy.alchemy.data.response.AlchemyNFT
import com.example.nftmarketplace.nft.alchemy.alchemy.data.response.AlchemyNFTs
import com.example.nftmarketplace.nft.alchemy.alchemy.data.response.OwnersResponse
import com.example.nftmarketplace.nft.alchemy.data.NFT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.coroutines.coroutineContext

@Component("AlchemyAPIAdapter")
class AlchemyAPIAdapter(
    @Autowired private val webClient: WebClient,
) {
    suspend fun getNFT(contractAddress: String, tokenId: String, withOwner: Boolean): NFT {
        with(CoroutineScope(coroutineContext)) {
            val nft = async {
                webClient.get()
                    .uri {
                        it.path("getNFTMetadata")
                            .queryParam("contractAddress", contractAddress)
                            .queryParam("tokenId", tokenId)
                            .queryParam("tokenType", "ERC721")
                            .build()
                    }.retrieveBodyOrThrowNotFound<AlchemyNFT>(
                        contractAddress,
                        tokenId.toLongOrNull()
                    )
            }
            if (!withOwner) return nft.await().toNFT()

            val owner = async { getNFTOwner(contractAddress, tokenId) }
            return nft.await().toNFT(owner.await())
        }
    }

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("owner", ownerAddress)
                    .build()
            }.retrieveBodyOrThrowNotFound<AlchemyNFTs>(
                ownerAddress,
                null
            )
        return nfts.ownedNfts.map {
            it.toNFT(ownerAddress)
        }
    }

    suspend fun getNFTsByOwner(contractAddress: String, ownerAddress: String?): List<NFT> {
        val nfts = webClient.get()
            .uri {
                it.path("getNFTs")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("owner", ownerAddress)
                    .queryParam("tokenType", "ERC721")
                    .build()
            }.retrieveBodyOrThrowNotFound<AlchemyNFTs>(
                contractAddress,
            null
        )
        return nfts.ownedNfts.map { it.toNFT() }
    }

    suspend fun getNFTOwner(contractAddress: String, tokenId: String): String {
        val nft = webClient.get()
            .uri {
                it.path("getOwnersForToken")
                    .queryParam("contractAddress", contractAddress)
                    .queryParam("tokenId", tokenId)
                    .build()
            }.retrieveBodyOrThrowNotFound<OwnersResponse>(
                contractAddress,
                tokenId.toLongOrNull()
            )
        return nft.owners.firstOrNull().orEmpty()
    }

    suspend fun getNFTsInBatch(contractAddress: List<String>, tokenIds: List<Long>): List<NFT> {
        require(contractAddress.size == tokenIds.size)
        val nfts = webClient.post()
            .uri { it.path("getNFTMetadataBatch").build() }.bodyValue(
                BatchNFTs(
                    tokens = contractAddress.zip(tokenIds).map { (contractAddress, tokenId) ->
                        TokenInfo(
                            contractAddress = contractAddress,
                            tokenId = tokenId,
//                            tokenType = "ERC721"
                        )
                    }
                )
            ).retrieve().awaitBody<List<AlchemyNFT>>()
        return nfts.map {
            val owner = getNFTOwner(it.contract.address, it.id.tokenId)
            it.toNFT(owner)
        }
    }

    private suspend inline fun <reified T : Any> WebClient.RequestHeadersSpec<*>
            .retrieveBodyOrThrowNotFound(
        contractAddress: String, tokenId: Long? = null
    ) = runCatching {
        retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) {
            throw NFTNotFoundException(contractAddress, tokenId)
        }.awaitBody<T>()
    }.getOrElse {
        throw NFTNotFoundException(contractAddress, tokenId)
    }
}

