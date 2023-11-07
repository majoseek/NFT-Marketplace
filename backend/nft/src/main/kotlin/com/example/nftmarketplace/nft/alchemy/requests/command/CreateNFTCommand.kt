package com.example.nftmarketplace.nft.alchemy.requests.command

data class CreateNFTCommand(
    val contractAddress: String,
    val tokenId: Long
)
