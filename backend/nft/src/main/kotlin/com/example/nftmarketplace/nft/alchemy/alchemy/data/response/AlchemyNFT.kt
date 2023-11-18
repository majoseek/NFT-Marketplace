package com.example.nftmarketplace.nft.alchemy.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class AlchemyNFT(
    @JsonProperty("contract")
    val contract: Contract,
    @JsonProperty("id")
    val id: Id,
    @JsonProperty("contractMetadata")
    val contractMetadata: ContractMetadata? = null,
    @JsonProperty("description")
    val description: String = "",
    @JsonProperty("media")
    val media: List<Media> = emptyList(),
    @JsonProperty("metadata")
    val metadata: Metadata? = null,
    @JsonProperty("title")
    val title: String = "",
    @JsonProperty("tokenUri")
    val tokenUri: TokenUri? = null
)
