package com.example.nftmarketplace.nft.alchemy.requests.command

data class TransferNFTCommand(
    val contractAddress: String,
    val tokenId: Long,
    val to: String,
)
