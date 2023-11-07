package com.example.nftmarketplace.projectionservice.db

import kotlinx.datetime.LocalDateTime
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface AuctionProjectionRepository : ReactiveMongoRepository<AuctionProjectionEntity, Long> {
    fun findByNftContractAddressAndNftTokenId(contractAddress: String, tokenId: Long): Mono<AuctionProjectionEntity?>

    fun findAllByNftOwnerAddress(ownerAddress: String): Flux<AuctionProjectionEntity>

    fun findAllByStatus(status: AuctionProjectionEntity.Status): Flux<AuctionProjectionEntity>

    fun findAllByNftContractAddress(contractAddress: String): Flux<AuctionProjectionEntity>

    fun findAllByExpiryTimeIsBeforeAndStatusEquals(time: LocalDateTime, status: AuctionProjectionEntity.Status): Flux<AuctionProjectionEntity>
}
