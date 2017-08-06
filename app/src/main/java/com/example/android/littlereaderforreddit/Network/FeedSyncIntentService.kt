package com.example.android.littlereaderforreddit.Network

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.Data.FeedDbHelper
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.Data.Feeds
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.Util.Constant
import retrofit2.Response

/**
 * Created by chuningluo on 17/8/4.
 */

class FeedSyncIntentService : IntentService("feed intent service") {

    override fun onHandleIntent(intent: Intent) {
        val lastLinkId: String? = intent.getStringExtra(Constant.EXTRA_LAST_LINK_ID)
        val response = RetrofitClient.instance.getFeeds(lastLinkId).execute()
        if (response.isSuccessful) {
            val redditResponse = response.body().data.children
            val feedDetails = ArrayList<FeedDetail>()
            redditResponse.forEach { feedDetails.add(it.data) }

            val briteDb = Db.getInstance(this)
            if (lastLinkId == null) {
                briteDb.writableDatabase.execSQL(FeedsModel.DELETEALL)
            }

            val insertFeed = FeedsModel.InsertFeed(briteDb.writableDatabase)
            val transaction = briteDb.newTransaction()
            try {
                for (detail in feedDetails) {
                    val largeImage = if (detail.preview == null) "" else detail.preview!!.images[0].source.url
                    insertFeed.bind(detail.id, detail.author, detail.title, detail.num_comments,
                            detail.created_formatted_time, detail.score, largeImage,
                            detail.self_text_html, detail.subreddit_name_prefixed)
                    briteDb.executeInsert(insertFeed.table, insertFeed.program)
                }
                transaction.markSuccessful()
            } finally {
                transaction.end()
            }

        }
    }
}
