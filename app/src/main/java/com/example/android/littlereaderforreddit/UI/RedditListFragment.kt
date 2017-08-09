package com.example.android.littlereaderforreddit.UI

import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        refreshFeedList()
        loaderManager.initLoader(SUBREDDIT_LOADER_ID, null, subredditLoaderListener)
        return view
    }

    private fun loadNextData(page: Int) {
        SyncUtils.startSyncForPaging(context, adapter.lastFeedId)
    }

    private fun refreshFeedList() {
        val query = FeedDetail.FACTORY.SelectAll()
        disposables.add(db.createQuery(query.tables, query.statement)
                .mapToList{cursor ->
                    FeedDetail.SELECT_ALL_MAPPER.map(cursor)
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
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
            showResults(data != null)
            if (data != null && data.data.children != null) {
                val subreddits = data.data.children
                val serializedStr = Subreddit.serialize(subreddits.map { it -> it.data })
                SharedPreferenceUtil.save(Constant.SUBREDDIT_LIST, serializedStr)
            }
        }
    }

    private fun showResults(hasResult: Boolean) {
        Log.d("Chuning", "show results " + hasResult)
        feeds_recycler.visibility = if (hasResult) View.VISIBLE else View.INVISIBLE
        error_message_display.visibility = if (hasResult) View.INVISIBLE else View.VISIBLE
    }
}