package io.ankburov.mlbridge.filter.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.*
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

/**
 * Catch uncaught errors, transform to a canonical error representation and put out the error state
 */
@Component
class UnknownErrorFilter(private val objectMapper: ObjectMapper) : AbstractErrorFilter(){

    private val log = loggerFor(javaClass)

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            log.error("Unknown error", throwable)

            val correlationId = this[CORRELATION_ID] as String?
            val responseModel = convertResponseFromError(correlationId, "UnknownError", ExceptionUtils.getRootCause(throwable))
            responseBody = objectMapper.writeValueAsString(responseModel)

            response.contentType = APPLICATION_JSON.mimeType
            responseStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()

            setErrorRaisedAndHandled()
        }
    }

    override fun shouldFilter(): Boolean {
        return RequestContext.getCurrentContext().errorRaised()
    }

    override fun filterOrder(): Int = -5
}