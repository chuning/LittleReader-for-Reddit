package com.example.android.littlereaderforreddit.Data

data class Comments(val data: CommentsData)

data class CommentsData(
        val children: List<CommentsChildren>
)

data class CommentsChildren(
        val data: CommentDetail?
)

//use custom serializer in retrofit
data class CommentDetail(
        val depth: Int,
        val body_html: String?,
        val author: String?,
        val created_utc: Long?,
        val score: Long?,
        val replies: Comments?,
        val count: Int?
)
