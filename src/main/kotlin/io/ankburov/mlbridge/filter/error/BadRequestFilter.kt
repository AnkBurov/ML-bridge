package io.ankburov.mlbridge.filter.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.*
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.Constants.ERROR
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class BadRequestFilter(private val objectMapper: ObjectMapper) : ZuulFilter(){

    private val log = loggerFor(javaClass)

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            log.error("Bad request", throwable)

            val correlationId = this[CORRELATION_ID] as String?
            val responseModel = convertResponseFromError(correlationId, "BadRequest", ExceptionUtils.getRootCause(throwable))
            responseBody = objectMapper.writeValueAsString(responseModel)

            response.contentType = APPLICATION_JSON.mimeType
            responseStatusCode = HttpStatus.BAD_REQUEST.value()

            setErrorRaisedAndHandled()
        }
    }

    override fun shouldFilter(): Boolean {
        return with(RequestContext.getCurrentContext()) {
            errorRaised() && ExceptionUtils.getRootCause(throwable) is IllegalArgumentException
        }
    }

    override fun filterType(): String = ERROR

    override fun filterOrder(): Int = -10
}