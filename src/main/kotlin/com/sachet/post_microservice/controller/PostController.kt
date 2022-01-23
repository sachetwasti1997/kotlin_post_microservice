package com.sachet.post_microservice.controller

import com.sachet.post_microservice.model.Post
import com.sachet.post_microservice.service.PostService
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/post")
class PostController(
    val postService: PostService
) {

    @PostMapping("/save")
    suspend fun savePost(@RequestBody post: Mono<Post>):ResponseEntity<Post>{
        val postReceived = post.awaitSingle()
        return ResponseEntity(postService.addPost(postReceived), HttpStatus.OK)
    }

    @GetMapping("")
    suspend fun getAllPost():List<Post> = postService.getAllPost()

    @GetMapping("/{userId}")
    suspend fun getPostByUserId(
        @PathVariable userId: String,
        @RequestParam page:Long,
        @RequestParam size:Long):ResponseEntity<List<Post>>{
        return ResponseEntity(postService.getPostByUserId(userId, page, size), HttpStatus.OK)
    }

    @PutMapping("/{postId}")
    suspend fun updatePost(
        @PathVariable postId:String,
        @RequestBody post: Mono<Post>
    ):ResponseEntity<Post> {
        val postReceived = post.awaitSingle()
        return ResponseEntity(postService.updatePost(postId, postReceived), HttpStatus.OK)
    }

    @DeleteMapping("/{postId}")
    suspend fun deletePost(@PathVariable postId: String):ResponseEntity<String>{
        postService.deletePost(postId)
        return ResponseEntity("Successfully deleted the post", HttpStatus.OK)
    }

    @DeleteMapping("/user/{userId}")
    suspend fun deletePostByUser(@PathVariable userId: String):ResponseEntity<String>{
        postService.deletePostsByUser(userId)
        return ResponseEntity("Successfully deleted all the posts of the user!",HttpStatus.OK)
    }

}