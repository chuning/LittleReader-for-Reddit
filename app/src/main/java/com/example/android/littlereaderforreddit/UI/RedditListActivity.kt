package com.example.android.littlereaderforreddit.UI

import android.content.Intent
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.Data.Feeds
import com.example.android.littlereaderforreddit.Network.RetrofitClient
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import kotlinx.android.synthetic.main.activity_reddit_list.*

class RedditListActivity : AppCompatActivity(), LoaderCallbacks<Feeds>, RedditFeedsAdapter.OnClickFeedItemListener {

    private val LOADER_ID = 100
    lateinit var adapter: RedditFeedsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_list)
        val recyclerView = this.feeds_recycler
        adapter = RedditFeedsAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun clickItem(detail: FeedDetail) {
        val intent = Intent(this, RedditDetailActivity::class.java)
        intent.putExtra(Constant.EXTRA_FEED_DETAIL, detail)
        startActivity(intent)
    }

    override fun onLoaderReset(loader: Loader<Feeds>?) {

    }

    override fun onLoadFinished(loader: Loader<Feeds>?, data: Feeds?) {
        loading_indicator.visibility = View.INVISIBLE
        adapter.feeds = data?.data?.children
        adapter.notifyDataSetChanged()
        showResults(data != null)
    }

    private fun showResults(hasResult: Boolean) {
        feeds_recycler.visibility = if (hasResult) View.VISIBLE else View.INVISIBLE
        error_message_display.visibility = if (hasResult) View.INVISIBLE else View.VISIBLE
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Feeds> {
        return object : AsyncTaskLoader<Feeds>(this) {
            var feeds: Feeds? = null
            override fun onStartLoading() {
                if (feeds != null) {
                    deliverResult(feeds)
                } else {
                    loading_indicator.visibility = View.VISIBLE
                    forceLoad()
                }
            }

            override fun loadInBackground(): Feeds? {
                try {
                    val response = RetrofitClient.instance.getFeeds().execute()
                    if (response.isSuccessful) {
                        return response.body()
                    }
                    return null
                } catch (e : Exception) {
                    e.printStackTrace()
                    return null
                }
            }

            override fun deliverResult(data: Feeds?) {
                feeds = data
                super.deliverResult(data)
            }
        }

    }

}


