package com.example.nftmarketplace.core

import com.example.nftmarketplace.core.data.DomainEvent

abstract class AggregateRoot {
    val events = mutableListOf<DomainEvent>()

    fun record(event: DomainEvent) {
        events.add(event)
    }

    fun record(events: List<DomainEvent>) {
        this.events.addAll(events)
    }

    fun clearEvents() {
        events.clear()
    }

    fun getEvents(): List<DomainEvent> {
        return events.toList()
    }
}
