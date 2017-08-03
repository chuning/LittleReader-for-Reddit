package com.example.android.littlereaderforreddit.UI

import android.content.Context
import android.content.Intent
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.Data.Feeds
import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.Data.SubredditResponse
import com.example.android.littlereaderforreddit.Network.RetrofitClient
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_reddit_list.*

class RedditListActivity : AppCompatActivity(), RedditFeedsAdapter.OnClickFeedItemListener {

    private val FEED_LOADER_ID = 100
    private val SUBREDDIT_LOADER_ID = 200
    lateinit var adapter: RedditFeedsAdapter
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_list)
        val recyclerView = this.feeds_recycler
        adapter = RedditFeedsAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        context = this
        supportLoaderManager.initLoader(FEED_LOADER_ID, null, feedLoaderListener)
        supportLoaderManager.initLoader(SUBREDDIT_LOADER_ID, null, subredditLoaderListener)
    }

    override fun clickItem(detail: FeedDetail) {
        val intent = Intent(this, RedditDetailActivity::class.java)
        intent.putExtra(Constant.EXTRA_FEED_DETAIL, detail)
        startActivity(intent)
    }

    private val subredditLoaderListener = object : LoaderCallbacks<SubredditResponse> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<SubredditResponse> {
            return object : AsyncTaskLoader<SubredditResponse>(context) {
                var subreddits: SubredditResponse? = null
                override fun onStartLoading() {
                    if (subreddits != null) {
                        deliverResult(subreddits)
                    } else {
                        loading_indicator.visibility = View.VISIBLE
                        forceLoad()
                    }
                }

                override fun loadInBackground(): SubredditResponse? {
                    try {
                        val response = RetrofitClient.instance.getSubreddits().execute()
                        if (response.isSuccessful) {
                            return response.body()
                        }
                        return null
                    } catch (e : Exception) {
                        e.printStackTrace()
                        return null
                    }
                }

                override fun deliverResult(data: SubredditResponse?) {
                    subreddits = data
                    super.deliverResult(data)
                }
            }
        }

        override fun onLoaderReset(loader: Loader<SubredditResponse>?) {
        }

        override fun onLoadFinished(loader: Loader<SubredditResponse>?, data: SubredditResponse?) {
            loading_indicator.visibility = View.INVISIBLE
            showResults(data != null)
            if (data != null && data.data.children != null) {
                val subreddits = data.data.children.toHashSet()
                val serializedStr = Subreddit.serialize(subreddits)
                SharedPreferenceUtil.save(Constant.SUBREDDIT_PREFERENCE, serializedStr)
            }
        }
    }

    private val feedLoaderListener = object : LoaderCallbacks<Feeds> {
        override fun onLoaderReset(loader: Loader<Feeds>?) {
        }

        override fun onLoadFinished(loader: Loader<Feeds>?, data: Feeds?) {
            loading_indicator.visibility = View.INVISIBLE
            adapter.feeds = data?.data?.children
            adapter.notifyDataSetChanged()
            showResults(data != null)
        }

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Feeds> {
            return object : AsyncTaskLoader<Feeds>(context) {
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

    private fun showResults(hasResult: Boolean) {
        feeds_recycler.visibility = if (hasResult) View.VISIBLE else View.INVISIBLE
        error_message_display.visibility = if (hasResult) View.INVISIBLE else View.VISIBLE
    }
}


