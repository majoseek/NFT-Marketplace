package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.alchemy.data.response.AlchemyNFT
import com.example.nftmarketplace.nft.data.FileExtension
import com.example.nftmarketplace.nft.data.NFT
import com.example.nftmarketplace.nft.db.NFTEntity
import com.example.nftmarketplace.nft.db.NFTId
import com.example.nftmarketplace.restapi.nfts.NFTResponse


fun AlchemyNFT.toNFT(
    ownerAddress: String? = null,
    type: NFT.Type? = null,
) = NFT(
    contractAddress = contract.address,
    tokenId = if (id.tokenId.startsWith("0x")) id.tokenId.substring(2).toLong(16) else id.tokenId.toLong(10),
    name = title,
    description = description,
    url = media.firstOrNull()?.raw ?: tokenUri?.raw.orEmpty(),
    type = type ?: FileExtension.getTypeFromExtension(media.firstOrNull()?.raw?.substringAfterLast(".")) ?: NFT.Type.Other,
    ownerAddress = ownerAddress,
)


fun NFT.toNFTResponse() = NFTResponse(
    contractAddress = contractAddress,
    tokenId = tokenId,
    name = name,
    description = description,
    url = url,
    type = NFTResponse.Type.valueOf(type.name),
    ownerAddress = ownerAddress,
)

fun NFT.toNFTEntity() = NFTEntity(
    id = NFTId(contractAddress, tokenId),
    name = name,
    description = description,
    url = url,
    type = NFTEntity.Type.valueOf(type.name),
)
