package com.example.nftmarketplace.nft.alchemy.data

import com.example.nftmarketplace.nft.NFT
import com.example.nftmarketplace.nft.data.FileExtension
import com.fasterxml.jackson.annotation.JsonProperty

class AlchemyNFTs {
    @JsonProperty("ownedNfts")
    val ownedNfts: List<AlchemyNFT> = emptyList()
}


fun AlchemyNFT.toNFT(ownerAddress: String? = null) = this.media.firstOrNull()?.raw.let { url ->
    NFT(
        contractAddress = contract.address,
        tokenID = id.tokenId.substringAfter("x").toLongOrNull(16),
        name = title,
        description = description,
        ownerAddress = ownerAddress,
        type = FileExtension.getTypeFromExtension(url?.substringAfterLast('.')),
        url = url,
    )
}
