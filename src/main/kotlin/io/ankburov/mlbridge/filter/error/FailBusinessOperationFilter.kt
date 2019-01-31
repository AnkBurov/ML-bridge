package io.ankburov.mlbridge.filter.error

import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.*
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * Fail business operation
 */
@Component
class FailBusinessOperationFilter : AbstractErrorFilter(){

    private val log = loggerFor(javaClass)

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            val correlationId = this[CORRELATION_ID] as String?
            MDC.put(CORRELATION_ID, correlationId)
            log.info("Failed")
        }
    }

    override fun shouldFilter(): Boolean {
        return with(RequestContext.getCurrentContext()) {
            errorRaised() && this.containsKey("BusinessOperationStarted")
        }
    }

    override fun filterOrder(): Int = -15
}