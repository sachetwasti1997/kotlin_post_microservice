package com.sachet.post_microservice.service

import com.sachet.post_microservice.error.PostDataException
import com.sachet.post_microservice.error.PostNotFoundException
import com.sachet.post_microservice.model.Post
import com.sachet.post_microservice.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.validation.Validator

@Service
class PostServiceImpl(
    val postRepository: PostRepository,
    val validator: Validator
):PostService{
    override suspend fun addPost(posts: Post): Post {
        validateRequest(posts)
        return postRepository.save(posts).awaitSingle()
    }

    override suspend fun getAllPost(): List<Post> {
        val postList = postRepository.findAll().asFlow().toList()
        if (postList.isEmpty()){
            throw PostNotFoundException("No Post Found")
        }
        return postList
    }

    override suspend fun getPostByUserId(userId: String, page: Long, size: Long): List<Post> {
        val postByUser = postRepository
            .findByUserId(userId)
            .skip(page * size)
            .take(size).asFlow().toList()
        if (postByUser.isEmpty()){
            throw PostNotFoundException("No post found for the user")
        }
        return postByUser
    }

    override suspend fun updatePost(postId: String, posts: Post): Post {
        val post = postRepository.findById(postId).awaitSingleOrNull()
        if (post == null){
            throw PostNotFoundException("No post found!")
        }
        post.description = posts.description
        post.title = posts.title
        return addPost(post)
    }

    override suspend fun deletePost(postId: String) {
       val post = postRepository.findById(postId).awaitSingleOrNull() ?: throw PostNotFoundException("Post Not Found!")
        postRepository.delete(post).awaitSingleOrNull()
    }

    override suspend fun deletePostsByUser(userId: String) {
        val postByUser = postRepository.findByUserId(userId).asFlow().toList()
        if (postByUser.isEmpty()){
            throw PostNotFoundException("No Post found for the user!")
        }
        postRepository.deleteAll(postByUser).awaitSingleOrNull()
    }

    fun validateRequest(post: Post){
        val constraintViolations = validator.validate(post)
        if (constraintViolations.size > 0){
            val errorStr = constraintViolations
                .stream()
                .map {
                    it.message
                }
                .collect(Collectors.joining(", "))

            throw PostDataException(errorStr)
        }
    }

}










