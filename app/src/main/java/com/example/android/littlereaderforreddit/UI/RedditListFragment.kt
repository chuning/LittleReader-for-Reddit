package com.example.android.littlereaderforreddit.UI

import android.accounts.NetworkErrorException
import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.android.littlereaderforreddit.Data.Db
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.Data.SubredditResponse
import com.example.android.littlereaderforreddit.Network.RetrofitClient
import com.example.android.littlereaderforreddit.Network.SyncUtils
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.SharedPreferenceUtil
import com.squareup.sqlbrite2.BriteDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_reddit_list.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class RedditListFragment: Fragment(), RedditFeedsAdapter.OnFeedItemClickListener,
        FilterDialogFragment.FilterDialogListener {
    private val SUBREDDIT_LOADER_ID = 200
    lateinit var adapter: RedditFeedsAdapter
    lateinit var db: BriteDatabase
    lateinit var disposables: CompositeDisposable
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(detail: FeedDetail)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnItemClickListener) {
            listener = context
        } else {
            throw ClassCastException(activity.toString() + " must implement ItemsListFragment.OnListItemSelectedListener");
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_reddit_list, container, false)
        val recyclerView = view?.findViewById(R.id.feeds_recycler) as RecyclerView
        adapter = RedditFeedsAdapter(activity, this)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                loadNextData(page)
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        db = Db.getInstance(context)
        disposables = CompositeDisposable()

        SyncUtils.initialize(context)
        loaderManager.initLoader(SUBREDDIT_LOADER_ID, null, subredditLoaderListener)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        refreshFeedList()
    }

    private fun loadNextData(page: Int) {
        SyncUtils.startSyncForPaging(context, adapter.lastFeedId)
    }

    private fun refreshFeedList() {
        loading_indicator.visibility = View.VISIBLE
        val query = FeedDetail.FACTORY.SelectAll()
        disposables.add(db.createQuery(query.tables, query.statement)
                .mapToList{cursor ->
                    FeedDetail.SELECT_ALL_MAPPER.map(cursor)
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { list ->
                    loading_indicator.visibility = View.INVISIBLE
                    if (list.isEmpty()) {
                        error_message_display.text = "Error fetching your feed."
                    }
                }
                .doOnError {
                    loading_indicator.visibility = View.INVISIBLE
                }
                .subscribe(adapter))

    }

    override fun onDestroyView() {
        disposables.dispose()
        super.onDestroyView()
    }

    override fun onItemClick(detail: FeedDetail) {
        listener?.onItemClick(detail)
    }

    override fun onFilterButtonClick() {
        val dialogFragment = FilterDialogFragment.newInstance()
        dialogFragment.setTargetFragment(this@RedditListFragment, 200)
        dialogFragment.show(fragmentManager, FilterDialogFragment::class.java.simpleName)
    }

    override fun onCickFilterDone() {
        loading_indicator.visibility = View.VISIBLE
        adapter.reset()
        scrollListener.resetState()
        SyncUtils.startImmediateSync(context)
    }

    private val subredditLoaderListener = object : LoaderManager.LoaderCallbacks<SubredditResponse> {
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
            showResults(true)
            if (data != null && data.data.children != null) {
                val subreddits = data.data.children
                val serializedStr = Subreddit.serialize(subreddits.map { it -> it.data })
                SharedPreferenceUtil.save(Constant.SUBREDDIT_LIST, serializedStr)
            } else {
                Toast.makeText(context, "Error fetching subscribed subreddits", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun showResults(show: Boolean) {
        Log.d("Chuning", "show results " + show)
        feeds_recycler.visibility = if (show) View.VISIBLE else View.INVISIBLE
        error_message_display.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }
}