package com.example.nftmarketplace.nft.alchemy

import com.example.nftmarketplace.nft.alchemy.data.AlchemyNFT
import com.example.nftmarketplace.nft.data.FileExtension
import com.example.nftmarketplace.nft.data.NFT
import com.example.nftmarketplace.nft.storage.db.NFTEntity
import com.example.nftmarketplace.nft.storage.db.NFTId
import com.example.nftmarketplace.restapi.nfts.NFTResponse

fun NFTEntity.toNFTResponse(): NFTResponse {
    return NFTResponse(
        contractAddress = id.contractAddress,
        tokenId = id.tokenId,
        name = name,
        description = description,
        url = url,
        type = NFTResponse.Type.valueOf(type.name),
        ownerAddress = ownerAddress,
    )
}

fun AlchemyNFT.toNFT(ownerAddress: String? = null) = NFT(
    contractAddress = this.contract.address,
    tokenId = id.tokenId.toLong(),
    name = title,
    description = description,
    url = tokenUri.raw,
    type = FileExtension.getTypeFromExtension(tokenUri.raw.substringAfterLast(".")),
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
    ownerAddress = ownerAddress,
)
