package io.ankburov.mlbridge.filter.pre

import com.netflix.zuul.ZuulFilter
import io.ankburov.mlbridge.utils.Constants.PRE

abstract class AbstractPreFilter : ZuulFilter() {

    override fun filterType(): String = PRE
}