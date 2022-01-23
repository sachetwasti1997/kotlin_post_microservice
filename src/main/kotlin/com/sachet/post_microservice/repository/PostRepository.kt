package com.sachet.post_microservice.repository

import com.sachet.post_microservice.model.Post
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PostRepository: ReactiveMongoRepository<Post, String> {

    fun findByUserId(userId:String):Flux<Post>
    fun findByPostId(postId:String):Mono<Post>
    fun deleteAllByUserId(userId: String)

}