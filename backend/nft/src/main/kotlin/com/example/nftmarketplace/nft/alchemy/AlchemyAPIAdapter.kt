package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.NFTNotFoundException
import com.example.nftmarketplace.nft.alchemy.data.bodyparams.BatchNFTs
import com.example.nftmarketplace.nft.alchemy.data.bodyparams.TokenInfo
import com.example.nftmarketplace.nft.alchemy.data.response.AlchemyNFT
import com.example.nftmarketplace.nft.alchemy.data.response.AlchemyNFTs
import com.example.nftmarketplace.nft.alchemy.data.response.OwnersResponse
import com.example.nftmarketplace.nft.data.FileExtension
import com.example.nftmarketplace.nft.data.NFT
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
    @Autowired private val nftFileTypeDetector: NFTFileTypeDetector,
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
                            .queryParam("refreshCache", true)
                            .build()
                    }.retrieveBodyOrThrowNotFound<AlchemyNFT>(
                        contractAddress,
                        tokenId.toLongOrNull()
                    )
            }
            if (!withOwner) {
                val response = nft.await()
                val type = getFileExtension(response)

                return response.toNFT(type = type)
            }
            val owner = async { getNFTOwner(contractAddress, tokenId) }
            return nft.await().toNFT(owner.await(), getFileExtension(nft.await()))
        }
    }

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFT> {
        val nfts = runCatching {
            webClient.get()
                .uri {
                    it.path("getNFTs")
                        .queryParam("owner", ownerAddress)
                        .queryParam("withMetadata", true)
                        .build()
                }.retrieveBodyOrThrowNotFound<AlchemyNFTs>(
                    ownerAddress,
                    null
                )
        }.getOrNull()
        return nfts?.ownedNfts?.map {
            it.toNFT(ownerAddress).copy(
                url = "https://altlayer-image-store.alt.technology/msnft.png"
            )
        }.orEmpty()
    }

    suspend fun getNFTsByOwner(contractAddress: String, ownerAddress: String?): List<NFT> {
        val nfts = runCatching {
            webClient.get()
                .uri {
                    it.path("getNFTs")
                        .queryParam("contractAddress", contractAddress)
                        .queryParam("owner", ownerAddress)
                        .queryParam("tokenType", "ERC721")
                        .build()
                }.retrieveBodyOrThrowNotFound<AlchemyNFTs>(
                    contractAddress = contractAddress,
                    tokenId = null
                )
        }.getOrNull()
        return nfts?.ownedNfts?.map { it.toNFT() }.orEmpty()
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
                        )
                    },
                    refreshCache = true
                )
            ).retrieve().awaitBody<List<AlchemyNFT>>()
        return nfts.map {
            val owner = getNFTOwner(it.contract.address, it.id.tokenId)
            val type = getFileExtension(it)
            it.toNFT(owner, type)
        }
    }

    private suspend inline fun <reified T : Any> WebClient.RequestHeadersSpec<*>.retrieveBodyOrThrowNotFound(
        contractAddress: String, tokenId: Long? = null,
    ) = runCatching {
        retrieve()
            .onStatus({ it.is4xxClientError || it.is5xxServerError }) {
                throw NFTNotFoundException(contractAddress, tokenId)
            }.awaitBody<T>()
    }.getOrElse {
        throw NFTNotFoundException(contractAddress, tokenId)
    }

    private suspend fun getFileExtension(alchemyNFT: AlchemyNFT): NFT.Type {
        val uri = alchemyNFT.media.firstOrNull()?.raw ?: alchemyNFT.tokenUri?.raw ?: return NFT.Type.Other
        return uri.substringAfterLast(".").takeIf { it.isNotEmpty() }?.let {
            FileExtension.getTypeFromExtension(extension = it)
        } ?: runCatching {
            nftFileTypeDetector.detectFileType(uri)
        }.getOrElse { NFT.Type.Other }
    }
}

