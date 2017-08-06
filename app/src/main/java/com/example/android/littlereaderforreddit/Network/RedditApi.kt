package com.example.android.littlereaderforreddit.Network

import com.example.android.littlereaderforreddit.Data.Comments
import com.example.android.littlereaderforreddit.Data.Feeds
import com.example.android.littlereaderforreddit.Data.SubredditResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RedditApi {
    @GET("/top.json")
    fun getRedditFeeds(@QueryMap paramMap: Map<String, String>): Call<Feeds>

    @GET("/comments/{id}.json")
    fun getComments(@Path("id") id: String): Call<List<Comments>>

    @GET("/subreddits/mine/subscriber.json")
    fun getSubreddits(): Call<SubredditResponse>
}