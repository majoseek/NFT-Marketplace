package com.example.nftmarketplace.nft

import com.example.nftmarketplace.core.data.NFTDomainModel
import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFT
import com.example.nftmarketplace.nft.data.FileExtension
import com.example.nftmarketplace.nft.storage.db.NFTEntity

fun AlchemyNFT.toNFT(ownerAddress: String? = null) = this.media.firstOrNull()?.raw.let { url ->
    NFTDomainModel(
        contractAddress = contract.address,
        tokenID = id.tokenId.substringAfter("0x").toLong(16),
        name = title,
        description = description,
        ownerAddress = ownerAddress,
        type = FileExtension.getTypeFromExtension(url?.substringAfterLast('.')),
        url = url,
    )
}


fun NFTEntity.toNFTDomainObject() = NFTDomainModel(
    contractAddress = id.contractAddress,
    tokenID = id.tokenId,
    name = name,
    ownerAddress = ownerAddress,
    url = url,
    description = description,
    type = NFTDomainModel.Type.valueOf(type.name),
)
