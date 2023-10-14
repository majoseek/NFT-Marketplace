package com.example.nftmarketplace

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

suspend fun <T>getOrPrintError(block: suspend () -> T) = runCatching {
    block()
}.getOrElse {
    it.printStackTrace()
    null
}
fun <T>T.getResponseEntity(statusIfNull: HttpStatus = HttpStatus.NOT_FOUND) =
    this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.status(statusIfNull).build()


inline fun <reified T : Enum<T>> enumValueOrNull(name: String): T? = enumValues<T>().find { it.name == name }
