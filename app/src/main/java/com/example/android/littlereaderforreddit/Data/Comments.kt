package com.example.android.littlereaderforreddit.Data


data class Comments(val data: CommentsData)

data class CommentsData(
        val children: List<CommentsChildren>
)

data class CommentsChildren(
        val data: CommentDetail?
)

data class CommentDetail(
        val depth: Int,
        val body: String?,
        val author: String?,
        val created: Long?,
        val score: Long?,
        val replies: Comments?,
        val count: Int?
)
