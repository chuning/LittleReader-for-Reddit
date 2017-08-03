package com.example.android.littlereaderforreddit.Data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by chuningluo on 17/8/2.
 */

data class Subreddit(
        val data: SubredditDetail
) {
    companion object {
        val hashsetType = object : TypeToken<HashSet<Subreddit>>(){}.type
        fun serialize(subreddits: Set<Subreddit>): String {
            return Gson().toJson(subreddits, hashsetType)
        }

        fun deserialize(string: String?): HashSet<Subreddit> {
            if (string == null) {
                return HashSet<Subreddit>()
            }
            return Gson().fromJson(string, hashsetType)
        }
    }
}

data class SubredditDetail(
        val display_name: String
)

data class SubredditResponse(
        val data: SubredditData
)

data class SubredditData(
        val children: List<Subreddit>?
)