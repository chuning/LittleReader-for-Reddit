package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.text.format.DateUtils
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.NotificationUtil
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil

/**
 * Created by chuningluo on 17/8/7.
 */
class FeedSyncTask {
    companion object {
        fun syncFeed(lastLinkId: String?, context: Context, attemptNotification: Boolean= false) {
            val response = RetrofitClient.instance.getFeeds(lastLinkId).execute()
            if (response.isSuccessful) {
                val redditResponse = response.body().data.children
                val feedDetails = redditResponse.map { it -> it.data }

                val briteDb = Db.getInstance(context)

                val insertFeed = FeedsModel.InsertFeed(briteDb.writableDatabase)
                val transaction = briteDb.newTransaction()
                try {
                    if (lastLinkId == null) {
                        briteDb.writableDatabase.execSQL(FeedsModel.DELETEALL)
                    }
                    for (detail in feedDetails) {
                        val largeImage = if (detail.preview == null) "" else detail.preview!!.images[0].source.url
                        insertFeed.bind(detail.id, detail.author, detail.title, detail.num_comments,
                                detail.created_formatted_time, detail.score, detail.thumbnail, largeImage,
                                detail.selftext_html, detail.subreddit)
                        briteDb.executeInsert(insertFeed.table, insertFeed.program)
                    }
                    transaction.markSuccessful()
                } finally {
                    transaction.end()
                }

                if (attemptNotification) {
                    NotificationUtil.notifyUserOfNewFeed(context, feedDetails.first())
                }
            }
        }
    }
}