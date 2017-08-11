package com.example.android.littlereaderforreddit.Network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant

/**
 * Created by chuningluo on 17/8/4.
 */

class FeedSyncIntentService : IntentService("feed intent service") {

    override fun onHandleIntent(intent: Intent) {
        if (intent.hasExtra(Constant.EXTRA_SYNC_FOR_PAGING)) {
            val lastLinkId: String? = intent.getStringExtra(Constant.EXTRA_LAST_LINK_ID)
            val isSuccess = SyncTask.syncFeed(lastLinkId, this)
            if (!isSuccess) {
                val message = this.resources.getString(R.string.error_fetching_feed)
                showToastInUiThread(this, message)
            }
        }
    }

    private fun showToastInUiThread(context: Context, message: String) {
        val mainThread = Handler(Looper.getMainLooper())
        mainThread.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}
