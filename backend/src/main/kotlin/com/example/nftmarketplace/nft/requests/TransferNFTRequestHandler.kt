package com.example.nftmarketplace.nft.requests

import com.example.nftmarketplace.nft.requests.command.TransferNFTCommand
import com.example.nftmarketplace.nft.storage.db.DbNFTRepository
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
