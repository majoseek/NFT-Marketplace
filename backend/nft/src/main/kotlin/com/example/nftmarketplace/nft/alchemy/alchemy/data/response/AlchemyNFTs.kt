package com.example.nftmarketplace.nft.alchemy.alchemy.data.response

import com.fasterxml.jackson.annotation.JsonProperty

class AlchemyNFTs {
    @JsonProperty("ownedNfts")
    val ownedNfts: List<AlchemyNFT> = emptyList()
}
