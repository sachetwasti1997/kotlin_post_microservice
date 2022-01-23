package com.sachet.post_microservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotNull

@Document("post")
data class Post(
    @Id
    var postId:String ?= null,
    @field: NotNull(message = "UserId cannot be null!")
    var userId:String ?= null,
    @field: NotNull(message = "Post title cannot be null")
    var title: String ?= null,
    var description: String ?= null
)
