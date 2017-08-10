package com.example.android.littlereaderforreddit.UI

import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Intent
import android.content.Loader
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.Comments
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.Network.RetrofitClient
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.Constant
import com.example.android.littlereaderforreddit.Util.DateTimeUtil
import com.example.android.littlereaderforreddit.Util.StringFormatUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.feed_detail.*
import kotlinx.android.synthetic.main.fragment_reddit_detail.*


class RedditDetailFragment: Fragment(), LoaderManager.LoaderCallbacks<List<Comments>> {
    lateinit var detail: FeedDetail
    lateinit var commentsAdapter: CommentsAdapter
    private val COMMENTS_LOADER = 1

    companion object {
        fun newInstance(detail: FeedDetail): RedditDetailFragment {
            val fragment = RedditDetailFragment()
            val args = Bundle()
            args.putParcelable(Constant.EXTRA_FEED_DETAIL, detail)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detail = arguments.getParcelable(Constant.EXTRA_FEED_DETAIL)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_reddit_detail, container, false)
        loaderManager.initLoader(COMMENTS_LOADER, null, this)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        subreddit_name.text = detail.subreddit
        created_time.text = String.format("·%s", DateTimeUtil.deltaTime(detail.created_time))
        score.text = detail.score.toString()
        num_comments.text = detail.num_comments.toString()
        feed_title.text = detail.title
        val image = detail.large_image
        if (hasThumbnailImage(image)) {
            Picasso.with(activity)
                    .load(image)
                    .into(imageView)
            if (detail.url != null) {
                link_url.setText(detail.url)
                link_url.visibility = View.VISIBLE
                imageView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(detail.url))
                    startActivity(intent)
                }
            }
        } else {
            imageView.visibility = View.GONE
        }

        if (detail.selftext_html.isNullOrEmpty()) {
            feed_body.visibility = View.GONE
        } else {
            val text = detail.selftext_html
            StringFormatUtil.formatHtml(text?:"", feed_body)
        }

        commentsAdapter = CommentsAdapter()
        comments_recycler.adapter = commentsAdapter
        comments_recycler.layoutManager = LinearLayoutManager(activity)
        ViewCompat.setNestedScrollingEnabled(comments_recycler, false)
    }

    override fun onLoaderReset(loader: Loader<List<Comments>>?) {
    }

    override fun onLoadFinished(loader: Loader<List<Comments>>?, commentList: List<Comments>?) {
        loading_indicator.visibility = View.GONE
        if (commentList?.size?:0 > 1) {
            commentsAdapter.setComments(commentList!![1])
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Comments>> {
        return object: AsyncTaskLoader<List<Comments>>(activity) {
            var comments: List<Comments>? = null
            override fun onStartLoading() {
                if (comments != null) {
                    deliverResult(comments)
                } else {
                    loading_indicator.visibility = View.VISIBLE
                    forceLoad()
                }
            }
            override fun loadInBackground(): List<Comments>? {
                try {
                    val response = RetrofitClient.instance.getComments(detail.id).execute()
                    if (response.isSuccessful) {
                        return response.body()
                    }
                    return null
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }

            override fun deliverResult(data: List<Comments>?) {
                comments = data
                super.deliverResult(data)
            }
        }
    }

    override fun onDestroyView() {
        imageView?.setImageDrawable(null)
        super.onDestroyView()
    }

    private fun hasThumbnailImage(url: String?): Boolean = url != null && url.contains("https")
}