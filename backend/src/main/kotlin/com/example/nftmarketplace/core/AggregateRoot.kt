package com.example.nftmarketplace.core

import com.example.nftmarketplace.core.data.DomainEvent

abstract class AggregateRoot {
    private val domainEvents = mutableListOf<DomainEvent>()

    fun record(event: DomainEvent) {
        domainEvents.add(event)
    }

    fun record(events: List<DomainEvent>) {
        this.domainEvents.addAll(events)
    }

    fun clearEvents() {
        domainEvents.clear()
    }

    fun getEvents(): List<DomainEvent> {
        return domainEvents.toList()
    }
}
