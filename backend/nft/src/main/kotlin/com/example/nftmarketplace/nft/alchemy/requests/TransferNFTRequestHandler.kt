package com.example.nftmarketplace.nft.alchemy.requests

import com.example.nftmarketplace.nft.alchemy.db.DbNFTRepository
import com.example.nftmarketplace.nft.alchemy.requests.command.TransferNFTCommand
import org.springframework.stereotype.Component

interface TransferNFTRequestHandler {
    suspend fun handle(command: TransferNFTCommand)
}

@Component
class TransferNFTRequestHandlerImpl(
    val dbNFTRepository: DbNFTRepository
) : TransferNFTRequestHandler {
    override suspend fun handle(command: TransferNFTCommand) {
        dbNFTRepository.get(command.contractAddress, command.tokenId)?.let { nft ->
            nft.transfer(command.to)
        }
    }
}
