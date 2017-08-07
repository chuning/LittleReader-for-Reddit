package com.example.android.littlereaderforreddit.Manager

import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.Data.Subreddit.Companion.deserialize
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil

/**
 * Created by chuningluo on 17/8/7.
 */

class SubredditsManager {
    companion object {
        private var allSubreddits: List<Subreddit>? = null
        private var excludedSubreddits: MutableSet<Subreddit>? = null

        fun getAllSubredditsList(): List<Subreddit> {
            return allSubreddits?: deserialize(SharedPreferenceUtil.get(Constant.SUBREDDIT_LIST))
        }

        fun getExcludedSubredditsList(): MutableSet<Subreddit> {
            return excludedSubreddits?: deserialize(SharedPreferenceUtil.get(Constant.SUBREDDIT_EXCLUDE_PREFERENCE)).toMutableSet()
        }

        fun setExcludedSubredditsList(excluded: MutableSet<Subreddit>) {
            excludedSubreddits = excluded
            SharedPreferenceUtil.save(Constant.SUBREDDIT_EXCLUDE_PREFERENCE, Subreddit.serialize(excluded.toList()))
        }

        fun getSelectedSubredditsList(): List<String> {
            return getAllSubredditsList().filter {it -> !getExcludedSubredditsList().contains(it) }
                    .map { it.display_name }
        }
    }
}