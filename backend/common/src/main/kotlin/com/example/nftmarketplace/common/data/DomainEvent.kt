package com.example.nftmarketplace.common.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.io.Serializable
import java.util.UUID

open class DomainEvent : Serializable {
    private val aggregateId: AggregateId
    val id: UUID
    val createdAt: java.time.Instant


    constructor(aggregateId: Long, id: UUID = UUID.randomUUID(), createdAt: Instant = Clock.System.now()) {
        this.id = id
        this.aggregateId = AggregateId.LongId(aggregateId)
        this.createdAt = createdAt.toJavaInstant()
    }

    constructor(aggregateId: Pair<Any, Any>, id: UUID = UUID.randomUUID(), createdAt: Instant = Clock.System.now()) {
        this.id = id
        this.aggregateId = AggregateId.CompoundId(aggregateId)
        this.createdAt = createdAt.toJavaInstant()
    }
}

private sealed interface AggregateId {
    @JvmInline
    value class LongId(val long: Long) : AggregateId

    @JvmInline
    value class CompoundId(val pair: Pair<Any, Any>) : AggregateId
}
