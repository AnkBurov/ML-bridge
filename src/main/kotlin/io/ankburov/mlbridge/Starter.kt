package io.ankburov.mlbridge

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@EnableZuulProxy
@SpringBootApplication
class Starter {//todo add consul

}

fun main(args: Array<String>) {
    SpringApplication.run(Starter::class.java, *args)
}