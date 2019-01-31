package io.ankburov.mlbridge.filter.error

import com.netflix.zuul.ZuulFilter
import io.ankburov.mlbridge.utils.Constants.ERROR

abstract class AbstractErrorFilter : ZuulFilter() {

    override fun filterType(): String = ERROR
}