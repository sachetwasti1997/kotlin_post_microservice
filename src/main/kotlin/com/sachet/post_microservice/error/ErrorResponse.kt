package com.sachet.post_microservice.error

import org.springframework.http.HttpStatus

class ErrorResponse(
    val message:String ?,
    val httpStatus: HttpStatus
){
    override fun toString(): String {
        return "{message='$message', httpStatus=$httpStatus}"
    }
}