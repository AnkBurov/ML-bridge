package io.ankburov.mlbridge.utils

import junit.framework.Assert.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> ResponseEntity<T?>.ok(message: String? = null): ResponseEntity<T?> {
    assertEquals(message, HttpStatus.OK, this.statusCode)
    return this
}

fun <T> ResponseEntity<T?>.badRequest(message: String? = null): ResponseEntity<T?> {
    assertEquals(message, HttpStatus.BAD_REQUEST, this.statusCode)
    return this
}

fun <T> ResponseEntity<T?>.internalError(message: String? = null): ResponseEntity<T?> {
    assertEquals(message, HttpStatus.INTERNAL_SERVER_ERROR, this.statusCode)
    return this
}

fun <T> ResponseEntity<T?>.bodyNotNull(message: String? = null) = this.body ?: throw AssertionError(message)
