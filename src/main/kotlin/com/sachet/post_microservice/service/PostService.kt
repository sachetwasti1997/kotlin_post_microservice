package com.sachet.post_microservice.service

import com.sachet.post_microservice.model.Post
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PostService {

    suspend fun addPost(posts: Post): Post
    suspend fun getAllPost(): List<Post>
    suspend fun getPostByUserId(userId:String, page:Long, size:Long): List<Post>
    suspend fun updatePost(postId:String, posts: Post): Post
    suspend fun deletePost(postId: String)
    suspend fun deletePostsByUser(userId: String)

}