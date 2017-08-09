package com.example.android.littlereaderforreddit.UI

import android.app.Fragment
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant

class RedditDetailActivity : FragmentActivity() {
    var fragment: RedditDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_detail)

        val detail: FeedDetail = intent.getParcelableExtra(Constant.EXTRA_FEED_DETAIL)
        if (savedInstanceState == null) {
            fragment = RedditDetailFragment.newInstance(detail)
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.reddit_detail_fragment, fragment)
            ft.commit()
        }
    }
}
