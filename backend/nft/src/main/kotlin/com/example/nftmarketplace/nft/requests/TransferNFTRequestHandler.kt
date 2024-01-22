package com.example.nftmarketplace.nft.requests

//interface TransferNFTRequestHandler {
//    suspend fun handle(command: TransferNFTCommand)
//}
//
//@Component
//class TransferNFTRequestHandlerImpl(
//    val dbNFTRepository: DbNFTRepository
//) : TransferNFTRequestHandler {
//    override suspend fun handle(command: TransferNFTCommand) {
//        dbNFTRepository.get(command.contractAddress, command.tokenId)?.let { nft ->
//            nft.transfer(command.to)
//            dbNFTRepository.save(nft)
//        }
//    }
//}
