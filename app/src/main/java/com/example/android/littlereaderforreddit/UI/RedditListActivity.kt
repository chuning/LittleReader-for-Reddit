package com.example.android.littlereaderforreddit.UI

import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.android.littlereaderforreddit.Data.*
import com.example.android.littlereaderforreddit.FeedsModel
import com.example.android.littlereaderforreddit.Network.RetrofitClient
import com.example.android.littlereaderforreddit.Network.SyncUtils
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import com.google.gson.Gson
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqlbrite2.SqlBrite
import com.squareup.sqlbrite2.mapToList
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_reddit_list.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class RedditListActivity : AppCompatActivity(), RedditFeedsAdapter.OnClickFeedItemListener {

    private val FEED_LOADER_ID = 100
    private val SUBREDDIT_LOADER_ID = 200
    lateinit var adapter: RedditFeedsAdapter
    lateinit var context: Context
    lateinit var db: BriteDatabase
    lateinit var disposable: Disposable
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit_list)
        val recyclerView = this.feeds_recycler
        adapter = RedditFeedsAdapter(this, this)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadNextData(page)
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        db = Db.getInstance(this)

        context = this
        supportLoaderManager.initLoader(SUBREDDIT_LOADER_ID, null, subredditLoaderListener)
        SyncUtils.initialize(this)
    }

    private fun loadNextData(page: Int) {
        SyncUtils.startSyncForPaging(context, adapter.getLastFeedId())
    }

    override fun onResume() {
        super.onResume()

        val query = FeedDetail.FACTORY.SelectAll()
        disposable = db.createQuery(query.tables, query.statement)
                .mapToList{cursor ->
                    FeedDetail.SELECT_ALL_MAPPER.map(cursor)
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter)

    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
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

    private fun showResults(hasResult: Boolean) {
        Log.d("Chuning", "show results " + hasResult)
        feeds_recycler.visibility = if (hasResult) View.VISIBLE else View.INVISIBLE
        error_message_display.visibility = if (hasResult) View.INVISIBLE else View.VISIBLE
    }
}


