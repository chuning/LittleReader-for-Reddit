package com.example.android.littlereaderforreddit.Data


data class Comments(val data: CommentsData): CommentsOrNull()

data class CommentsData(
        val children: List<CommentsChildren>
)

data class CommentsChildren(
        val data: CommentDetail?
)

data class CommentDetail(
        val body: String?,
        val author: String?,
        val depth: Int,
        val created: Long?,
        val score: Long?,
        val replies: CommentsOrNull?,
        val count: Int?
)

open class CommentsOrNull()