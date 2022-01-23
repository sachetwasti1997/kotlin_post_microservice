package com.sachet.post_microservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostMicroserviceApplication

fun main(args: Array<String>) {
    runApplication<PostMicroserviceApplication>(*args)
}
