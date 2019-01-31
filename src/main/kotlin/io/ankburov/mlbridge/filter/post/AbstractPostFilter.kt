package io.ankburov.mlbridge.filter.post

import com.netflix.zuul.ZuulFilter
import io.ankburov.mlbridge.utils.Constants.POST

abstract class AbstractPostFilter : ZuulFilter() {

    override fun filterType(): String = POST
}