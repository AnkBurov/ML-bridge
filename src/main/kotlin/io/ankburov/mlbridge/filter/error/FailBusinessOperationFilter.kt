package io.ankburov.mlbridge.filter.error

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.*
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.Constants.ERROR
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class FailBusinessOperationFilter : ZuulFilter(){

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

    override fun filterType(): String = ERROR

    override fun filterOrder(): Int = -15
}