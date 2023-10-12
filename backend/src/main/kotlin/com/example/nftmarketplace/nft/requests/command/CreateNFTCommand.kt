package com.example.nftmarketplace.nft.requests.command

data class CreateNFTCommand(
    val contractAddress: String,
    val tokenId: Long
)
