package com.example.android.littlereaderforreddit.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant

class RedditDetailActivity : AppCompatActivity() {
    var fragment: RedditDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeActionContentDescription(getString(R.string.back_button))

        val detail: FeedDetail = intent.getParcelableExtra(Constant.EXTRA_FEED_DETAIL)
        if (savedInstanceState == null) {
            fragment = RedditDetailFragment.newInstance(detail)
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.reddit_detail_fragment, fragment)
            ft.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
