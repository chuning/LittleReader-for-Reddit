package com.example.android.littlereaderforreddit.Network

import android.app.IntentService
import android.content.Intent
import com.example.android.littlereaderforreddit.Data.FeedDbHelper
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.FeedsModel

/**
 * Created by chuningluo on 17/8/4.
 */

class FeedSyncIntentService : IntentService("intent service") {

    override fun onHandleIntent(intent: Intent?) {
        val response = RetrofitClient.instance.getFeeds().execute()
        if (response.isSuccessful) {
            val redditResponse = response.body().data.children
            val feedDetails = ArrayList<FeedDetail>()
            redditResponse.forEach { feedDetails.add(it.data) }

            val db = FeedDbHelper.getInstance(this).writableDatabase
            db.execSQL(FeedsModel.DELETEALL)

            val insertFeed = FeedsModel.InsertFeed(db)
            for (detail in feedDetails) {
                val largeImage = if (detail.preview == null) "" else detail.preview!!.images[0].source.url
                insertFeed.bind(detail.id, detail.author, detail.title, detail.num_comments,
                        detail.created_formatted_time, detail.score, largeImage,
                        detail.self_text_html, detail.subreddit_name_prefixed)
                insertFeed.program.execute()
            }
        }
    }
}
