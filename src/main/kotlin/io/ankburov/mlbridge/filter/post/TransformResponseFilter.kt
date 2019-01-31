package io.ankburov.mlbridge.filter.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.errorRaised
import io.ankburov.mlbridge.utils.isMethod
import io.ankburov.mlbridge.utils.transformToCanonicalResponse
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

/**
 * Transform external service response to a canonical model response
 */
@Component
class TransformResponseFilter(private val objectMapper: ObjectMapper) : AbstractPostFilter() {

    override fun shouldFilter(): Boolean {
        return with(RequestContext.getCurrentContext()) {
            request.isMethod(HttpMethod.POST) && !errorRaised() && responseBody != null
        }
    }

    override fun filterOrder(): Int = Int.MIN_VALUE + 2

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            val correlationId = this[CORRELATION_ID] as String?
            val transformedResponse = objectMapper.createObjectNode().transformToCanonicalResponse(correlationId, responseBody)
            responseBody = objectMapper.writeValueAsString(transformedResponse)
        }
    }
}