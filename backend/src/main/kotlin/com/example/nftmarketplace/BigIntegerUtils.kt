package com.example.nftmarketplace

import java.math.BigInteger

infix operator fun BigInteger.rangeUntil(other: BigInteger) =
    BigIntegerRange(this, other - BigInteger.ONE)

infix operator fun BigInteger.rangeTo(other: BigInteger) =
    BigIntegerRange(this, other)

class BigIntegerRange(
    override val start: BigInteger,
    override val endInclusive: BigInteger
) : ClosedRange<BigInteger>, Iterable<BigInteger> {
    override operator fun iterator(): Iterator<BigInteger> =
        BigIntegerRangeIterator(this)
}

class BigIntegerRangeIterator(
    private val range: ClosedRange<BigInteger>
) : Iterator<BigInteger> {
    private var current = range.start

    override fun hasNext(): Boolean =
        current <= range.endInclusive

    override fun next(): BigInteger {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return current++
    }
}
