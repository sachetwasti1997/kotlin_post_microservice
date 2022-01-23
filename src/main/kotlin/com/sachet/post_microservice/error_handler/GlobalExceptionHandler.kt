package com.sachet.post_microservice.error_handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.sachet.post_microservice.error.ErrorResponse
import com.sachet.post_microservice.error.PostDataException
import com.sachet.post_microservice.error.PostNotFoundException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalExceptionHandler: ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val dataFactory = exchange.response.bufferFactory()
        val errorResponse: ErrorResponse
        if (ex is PostNotFoundException){
            errorResponse = ErrorResponse(ex.message, HttpStatus.NOT_FOUND)
            exchange.response.statusCode = HttpStatus.NOT_FOUND
        }else if(ex is PostDataException){
            errorResponse = ErrorResponse(ex.message, HttpStatus.BAD_REQUEST)
            exchange.response.statusCode = HttpStatus.BAD_REQUEST
        }else{
            errorResponse = ErrorResponse(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
            exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        }
        val mapper = ObjectMapper()
        val jsonString = mapper.writeValueAsString(errorResponse)
        val errorMessage = dataFactory.wrap(jsonString.toByteArray())
        return exchange.response.writeWith(Mono.just(errorMessage))
    }
}