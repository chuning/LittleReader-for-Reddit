package com.example.android.littlereaderforreddit.UI

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Menu
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import android.widget.FrameLayout



class RedditListActivity : FragmentActivity(), RedditListFragment.OnItemClickListener {
    private var isTwoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_list)
        determinePaneLayout()
    }

    private fun determinePaneLayout() {
        val fragmentItemDetail = findViewById(R.id.reddit_detail_fragment) as FrameLayout?
        if (fragmentItemDetail != null) {
            isTwoPane = true
//            val fragmentItemsList = supportFragmentManager.findFragmentById(R.id.reddit_list_fragment) as RedditListFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onItemClick(detail: FeedDetail) {
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


