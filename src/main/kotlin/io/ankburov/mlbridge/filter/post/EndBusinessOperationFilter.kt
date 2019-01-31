package io.ankburov.mlbridge.filter.post

import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.errorRaised
import io.ankburov.mlbridge.utils.isMethod
import io.ankburov.mlbridge.utils.loggerFor
import org.slf4j.MDC
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

/**
 * End business operation
 */
@Component
class EndBusinessOperationFilter : AbstractPostFilter() {

    private val log = loggerFor(javaClass)

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            val correlationId = this[CORRELATION_ID] as String?
            MDC.put(CORRELATION_ID, correlationId)
            log.info("Completed")
        }
    }

    override fun shouldFilter(): Boolean {
        return with(RequestContext.getCurrentContext()) {
            request.isMethod(HttpMethod.POST) && !errorRaised() && this.containsKey("BusinessOperationStarted")
        }
    }

    override fun filterOrder(): Int = Int.MIN_VALUE + 1
}