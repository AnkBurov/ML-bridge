package io.ankburov.mlbridge.utils

import com.netflix.zuul.context.RequestContext
import io.ankburov.mlbridge.model.Response
import io.ankburov.mlbridge.model.ResponseTechData
import io.ankburov.mlbridge.utils.Constants.ERROR_HAPPENED
import io.ankburov.mlbridge.utils.Constants.THROWABLE
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import javax.servlet.http.HttpServletRequest

fun HttpServletRequest?.isMethod(method: HttpMethod): Boolean {
    return this?.let { method.matches(it.method) } ?: false
}

fun <T> loggerFor(clazz: Class<T>) = LoggerFactory.getLogger(clazz)

fun convertResponseFromError(correlationId: String?, errorCode: String, throwable: Throwable): Response {
    return Response(ResponseTechData(correlationId, errorCode, throwable.message))
}

fun RequestContext.setErrorRaisedAndHandled() {
    set(ERROR_HAPPENED, true)
    remove(THROWABLE) // disable following error handlers for this request
}

fun RequestContext.errorRaised(): Boolean {
    return getBoolean(ERROR_HAPPENED, false) || get(THROWABLE) != null
}

