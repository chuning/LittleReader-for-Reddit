package com.example.android.littlereaderforreddit.Network

import android.content.Context
import android.text.format.DateUtils
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.Data.Token
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.UI.LoginActivity
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.NotificationUtil
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import retrofit2.Call
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by chuningluo on 17/8/7.
 */
class SyncTask {
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

        fun syncAuth(isRefresh: Boolean): Boolean {
            try {
                val call = if (isRefresh) AuthClient.instance.refreshToken() else
                    AuthClient.instance.getToken(Constant.REDIRECT_URI)

                val response = call.execute()
                if (response.isSuccessful) {
                    val token = response.body()
                    if (token.access_token != null) {
                        val expiresTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(token.expires_in!!)
                        SharedPreferenceUtil.save(Constant.ACCESS_TOKEN, token.access_token)
                        SharedPreferenceUtil.saveLong(Constant.EXPIRE_TIME, expiresTime)
                    }
                    if (token.refresh_token != null) {
                        SharedPreferenceUtil.save(Constant.REFRESH_TOKEN, token.refresh_token)
                    }
                    return true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }
    }
}