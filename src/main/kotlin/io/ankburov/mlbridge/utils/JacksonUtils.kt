package io.ankburov.mlbridge.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.RawValue

fun JsonNode.getCorrelationId(): String? {
    return     this.get("techData")?.get("correlationId")?.asText()
}

fun JsonNode.getDataNode(): JsonNode? {
    return this.get("businessData")?.get("data")
}

fun ObjectNode.transformToCanonicalResponse(correlationId: String?, data: String): ObjectNode {
    this.putObject("techData").put("correlationId", correlationId)
    this.putObject("businessData").putRawValue("data", RawValue(data))
    return this
}