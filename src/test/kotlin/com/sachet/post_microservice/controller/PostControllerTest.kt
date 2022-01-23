package com.sachet.post_microservice.controller

import com.sachet.post_microservice.model.Post
import com.sachet.post_microservice.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class PostControllerTest
@Autowired
constructor(
    val postRepository: PostRepository,
    val webTestClient: WebTestClient
) {

    var id:String ?= null

    @BeforeEach
    fun setUp() {
        runBlocking {
            id = UUID.randomUUID().toString()
            val postList = listOf<Post>( Post(
                userId = id,
                title = "Auto 100 Testing",
                description = "This is Testing 1",
                ),Post(
                    userId = id,
                    title = "Auto 101 Testing",
                    description = "This is Testing 2",
                ),Post(
                    userId = id,
                    title = "Auto 102 Testing",
                    description = "This is Testing 3",
                ),Post(
                    postId = id,
                    userId = id,
                    title = "Auto 103 Testing",
                    description = "This is Testing 4",
                )
            )
            withContext(Dispatchers.IO) {
                postRepository.saveAll(postList).blockLast()
            }
        }
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            withContext(Dispatchers.IO) {
                postRepository.deleteAll().block()
            }
        }
    }

    @Test
    fun savePost() {

        val post = Post(
            userId = UUID.randomUUID().toString(),
            title = "Auto Random Testing",
            description = "This is Testing Random!",
        )

        webTestClient
            .post()
            .uri("/api/v1/post/save")
            .bodyValue(post)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Post::class.java)
            .consumeWith {
                val postSaved = it.responseBody
                Assertions.assertNotNull(postSaved?.postId)
            }

    }

    @Test
    fun getAllTest(){
        webTestClient
            .get()
            .uri("/api/v1/post")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(Post::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Post>> {
                val post = it.responseBody
                Assertions.assertEquals(4, post?.size)
            }
    }

    @Test
    fun getAllPostByUserIdFirstPage(){
        webTestClient
            .get()
            .uri {
                it
                    .path("/api/v1/post/$id")
                    .queryParam("page", 0)
                    .queryParam("size", 3)
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(Post::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Post>> {
                val postList = it.responseBody
                Assertions.assertEquals(3, postList?.size)
            }
    }

    @Test
    fun getAllPostByUserIdSecondPage(){
        webTestClient
            .get()
            .uri {
                it
                    .path("/api/v1/post/$id")
                    .queryParam("page", 1)
                    .queryParam("size", 3)
                    .build()
            }
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(Post::class.java)
            .consumeWith<WebTestClient.ListBodySpec<Post>> {
                val postList = it.responseBody
                Assertions.assertEquals(1, postList?.size)
            }
    }

    @Test
    fun updatePost(){
        val updatedTitle = "Auto 10three Testing"
        val updatedDes = "This is Testing Four"
        val postToUpdate = Post(
            postId = id,
            userId = id,
            title = updatedTitle,
            description = updatedDes,
        )

        webTestClient
            .put()
            .uri("/api/v1/post/$id")
            .bodyValue(postToUpdate)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Post::class.java)
            .consumeWith {
                val updatedPost = it.responseBody
                Assertions.assertEquals(updatedTitle, updatedPost?.title)
                Assertions.assertEquals(updatedDes, updatedPost?.description)
            }
    }

    @Test
    fun getPostByUserIdNotExist(){
        webTestClient
            .get()
            .uri{
                it
                    .path("/api/v1/post/${UUID.randomUUID()}")
                    .queryParam("page", 0)
                    .queryParam("size", 3)
                    .build()
            }
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody(String::class.java)
            .consumeWith {
                val message = it?.responseBody
                message?.contains("No post found for the user")?.let { it1 -> Assertions.assertTrue(it1) }
                println(message)
            }
    }

    @Test
    fun updatePostPostIdNotExist(){
        val post = Post(
            userId = id,
            title = "Auto 10One Testing",
            description = "This is Testing Two",
        )

        webTestClient
            .put()
            .uri("/api/v1/post/${UUID.randomUUID()}")
            .bodyValue(post)
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody(String::class.java)
            .consumeWith {
                val message = it.responseBody
                message?.contains("No post found!")?.let { it1 -> Assertions.assertTrue(it1) }
            }
    }

    @Test
    fun deletePost(){
        webTestClient
            .delete()
            .uri("/api/v1/post/$id")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun deletePostNotExist(){
        webTestClient
            .delete()
            .uri("/api/v1/post/${UUID.randomUUID()}")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun deleteUsersPost(){
        webTestClient
            .delete()
            .uri("/api/v1/post/user/$id")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(String::class.java)
            .consumeWith {
                val message = it.responseBody
                Assertions.assertEquals("Successfully deleted all the posts of the user!", message)
            }
    }

    @Test
    fun deleteAllPostsByUserInvalid(){
        webTestClient
            .delete()
            .uri("/api/v1/post/user/${UUID.randomUUID()}")
            .exchange()
            .expectStatus()
            .isNotFound
            .expectBody(String::class.java)
            .consumeWith {
                val message = it.responseBody
                message?.contains("No Post found for the user!")?.let { it1 -> Assertions.assertTrue(it1) }
            }
    }

}