package com.example.android.littlereaderforreddit.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import android.widget.FrameLayout
import com.example.android.littlereaderforreddit.Manager.UserManager


class RedditListActivity : AppCompatActivity(), RedditListFragment.OnItemClickListener {
    private var isTwoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_list)
        if (!UserManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            determinePaneLayout()
        }
    }

    private fun determinePaneLayout() {
        val fragmentItemDetail = findViewById(R.id.reddit_detail_fragment) as FrameLayout?
        if (fragmentItemDetail != null) {
            isTwoPane = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onFeedClick(detail: FeedDetail) {
        if (isTwoPane) {
            val detailFragment = RedditDetailFragment.newInstance(detail)
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.reddit_detail_fragment, detailFragment)
            ft.commit()

        } else {
            val intent = Intent(this, RedditDetailActivity::class.java)
            intent.putExtra(Constant.EXTRA_FEED_DETAIL, detail)
            startActivity(intent)
        }
    }
}


