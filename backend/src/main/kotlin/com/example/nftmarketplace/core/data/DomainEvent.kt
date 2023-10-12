package com.example.nftmarketplace.core.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

open class DomainEvent(
    val id: UUID = UUID.randomUUID(),
    val aggregateId: Long,
    val createdAt: Instant = Clock.System.now(),
)
