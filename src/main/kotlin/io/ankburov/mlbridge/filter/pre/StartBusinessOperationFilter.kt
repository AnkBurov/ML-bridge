package io.ankburov.mlbridge.filter.pre

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.utils.Constants.BUSINESS_OPERATION_STARTED
import io.ankburov.mlbridge.utils.Constants.CORRELATION_ID
import io.ankburov.mlbridge.utils.Constants.PRE
import io.ankburov.mlbridge.utils.isMethod
import io.ankburov.mlbridge.utils.loggerFor
import org.slf4j.MDC
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

@Component
class StartBusinessOperationFilter : ZuulFilter() {

    private val log = loggerFor(javaClass)

    override fun run(): Any? {
        return RequestContext.getCurrentContext().apply {
            val correlationId = this[CORRELATION_ID] as String?
            MDC.put(CORRELATION_ID, correlationId)
            log.info("Started")
            this[BUSINESS_OPERATION_STARTED] = true
        }
    }

    override fun shouldFilter(): Boolean = RequestContext.getCurrentContext().request.isMethod(HttpMethod.POST)

    override fun filterType(): String = PRE

    override fun filterOrder(): Int = Int.MIN_VALUE + 1
}