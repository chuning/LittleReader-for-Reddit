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
            FeedSyncTask.syncFeed(lastLinkId, this)
        }
    }

}
