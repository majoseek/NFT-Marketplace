package com.example.nftmarketplace.nft.requests

import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.data.NFT
import com.example.nftmarketplace.nft.db.DbNFTRepository
import com.example.nftmarketplace.nft.requests.command.CreateNFTBatchCommand
import com.example.nftmarketplace.nft.requests.command.CreateNFTCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface CreateNFTRequestHandler {
    suspend fun handle(command: CreateNFTCommand)

    suspend fun handleBatch(command: CreateNFTBatchCommand)
}

@Component
class CrateNFTRequestHandlerImpl(
    @Autowired private val alchemyAPIAdapter: AlchemyAPIAdapter,
    @Autowired private val nftRepository: DbNFTRepository,
) : CreateNFTRequestHandler {
    override suspend fun handle(command: CreateNFTCommand) {
        val (contractAddress, tokenId) = command
        val nft = alchemyAPIAdapter.getNFT(contractAddress, tokenId.toString(), false).let {
            NFT.create(
                contractAddress = it.contractAddress,
                tokenId = it.tokenId,
                name = it.name,
                description = it.description,
                url = it.url,
                type = it.type,
            )
        }
        nftRepository.create(nft)
    }

    override suspend fun handleBatch(command: CreateNFTBatchCommand) {
        val contractAddresses = command.commands.map { it.contractAddress }
        val tokenIds = command.commands.map { it.tokenId }
        val nfts = alchemyAPIAdapter.getNFTsInBatch(contractAddresses, tokenIds).map {
            NFT.create(
                contractAddress = it.contractAddress,
                tokenId = it.tokenId,
                name = it.name,
                description = it.description,
                url = it.url,
                type = it.type,
            )
        }
        nftRepository.saveAll(nfts)
    }
}
