package com.example.android.littlereaderforreddit.Network

import android.app.IntentService
import android.content.Intent
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.Util.Constant

/**
 * Created by chuningluo on 17/8/4.
 */

class FeedSyncIntentService : IntentService("feed intent service") {

    override fun onHandleIntent(intent: Intent) {
        if (intent.hasExtra(Constant.EXTRA_SYNC_FOR_PAGING)) {
            val lastLinkId: String? = intent.getStringExtra(Constant.EXTRA_LAST_LINK_ID)
            val response = RetrofitClient.instance.getFeeds(lastLinkId).execute()
            if (response.isSuccessful) {
                val redditResponse = response.body().data.children
                val feedDetails = redditResponse.map { it -> it.data }

                val briteDb = Db.getInstance(this)

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
            }
        }
    }

}
