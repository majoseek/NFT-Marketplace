package com.example.nftmarketplace.core.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

open class DomainEvent {
    private val aggregateId: AggregateId
    val id: UUID
    val createdAt: Instant


    constructor(aggregateId: Long, id: UUID = UUID.randomUUID(), createdAt: Instant = Clock.System.now()) {
        this.id = id
        this.aggregateId = AggregateId.LongId(aggregateId)
        this.createdAt = createdAt
    }

    constructor(aggregateId: Pair<Any, Any>, id: UUID = UUID.randomUUID(), createdAt: Instant = Clock.System.now()) {
        this.id = id
        this.aggregateId = AggregateId.CompoundId(aggregateId)
        this.createdAt = createdAt
    }
}

private sealed interface AggregateId {
    @JvmInline
    value class LongId(val long: Long) : AggregateId

    @JvmInline
    value class CompoundId(val pair: Pair<Any, Any>) : AggregateId
}
