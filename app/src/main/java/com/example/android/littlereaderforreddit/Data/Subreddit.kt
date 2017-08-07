package com.example.android.littlereaderforreddit.Data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by chuningluo on 17/8/2.
 */

data class SubredditDetail(
        val data: Subreddit
)

data class Subreddit(
        val display_name: String
) {
    companion object {
        val listType = object : TypeToken<List<Subreddit>>(){}.type
        fun serialize(subreddits: List<Subreddit>): String {
            return Gson().toJson(subreddits, listType)
        }

        fun deserialize(string: String?): List<Subreddit> {
            if (string == null) {
                return emptyList()
            }
            return Gson().fromJson(string, listType)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other!= null && other is Subreddit) {
            return this.display_name == other.display_name
        }
        return false
    }

    override fun hashCode(): Int {
        return this.display_name.hashCode()
    }
}

data class SubredditResponse(
        val data: SubredditData
)

data class SubredditData(
        val children: List<SubredditDetail>?
)