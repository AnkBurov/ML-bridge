package io.ankburov.mlbridge

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
class Starter {

}

fun main(args: Array<String>) {
    SpringApplication.run(Starter::class.java, *args)
}