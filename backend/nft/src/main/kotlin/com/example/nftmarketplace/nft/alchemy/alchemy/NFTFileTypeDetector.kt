package com.example.nftmarketplace.nft.alchemy.alchemy

import com.example.nftmarketplace.nft.alchemy.data.NFT
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class NFTFileTypeDetector(
    @Autowired private val webClient: WebClient,
) {
    suspend fun detectFileType(ipfsUri: String): NFT.Type {
        return try {
            // Fetch the content as a ByteArray
            val response = webClient.get()
                .uri(ipfsUri)
                .retrieve()
                .toBodilessEntity()
                .awaitSingle()

            getFromMimeType(response.headers.contentType?.toString())
        } catch (e: WebClientResponseException) {
            throw FileTypeDetectionException("WebClientResponseException: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw FileTypeDetectionException("Exception occurred while detecting file type: ${e.localizedMessage}")
        }
    }

    private fun getFromMimeType(mimeType: String?): NFT.Type {
        return when {
            mimeType == null -> NFT.Type.Other
            mimeType.startsWith("image") -> NFT.Type.Image
            mimeType.startsWith("video") -> NFT.Type.Video
            mimeType.startsWith("audio") -> NFT.Type.Audio
            mimeType.startsWith("text") -> NFT.Type.Text
            mimeType.startsWith("application/pdf") -> NFT.Type.Text
            else -> NFT.Type.Other
        }
    }
}

class FileTypeDetectionException(message: String) : RuntimeException(message)
