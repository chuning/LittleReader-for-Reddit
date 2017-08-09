package com.example.android.littlereaderforreddit.UI

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.FeedDetail
import com.example.android.littlereaderforreddit.R
import com.squareup.picasso.Picasso
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.feed_list_header_item.view.*

class RedditFeedsAdapter(val context: Context, val listener: OnFeedItemClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Consumer<MutableList<FeedDetail>> {

    override fun accept(list: MutableList<FeedDetail>) {
        if (list.isNotEmpty()) {
            lastFeedId = list.last().id
        }
        feeds = list
        Log.d("Chuning", "accept list" + list.size)
        notifyDataSetChanged()
    }

    private var feeds: List<FeedDetail>? = null
    var lastFeedId: String? = null
    private val FEED_ITEM_NO_THUMBNAIL = 0
    private val FEED_ITEM_WITH_THUMBNAIL = 1
    private val LIST_VIEW_HEADER = 2

    interface OnFeedItemClickListener {
        fun onItemClick(detail: FeedDetail)
        fun onFilterButtonClick()
    }

    fun reset() {
        lastFeedId = null
        feeds = null
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return (feeds?.size ?: 0) + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListHeaderViewHolder) {
            holder.itemView.filter.setOnClickListener {
                listener.onFilterButtonClick()
            }
        } else if (holder is RedditFeedsViewHolder) {
            val feedDetail: FeedDetail = feeds!![position - 1]
            holder.itemView.subreddit_name.text = feedDetail.subreddit
            holder.itemView.created_time.text = feedDetail.created_formatted_time
            holder.itemView.score.text = feedDetail.score.toString()
            holder.itemView.comments.text = feedDetail.num_comments.toString()
            holder.itemView.feed_title.text = feedDetail.title
            if (hasThumbnailImage(feedDetail.thumbnail)) {
                Picasso.with(context)
                        .load(feedDetail.thumbnail)
                        .into(holder.itemView.thumbnail)
            }
            holder.itemView.setOnClickListener {
                listener.onItemClick(feedDetail)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == LIST_VIEW_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_list_header_item, parent, false)
            return ListHeaderViewHolder(view)
        }
        val layoutResId = if (viewType == FEED_ITEM_WITH_THUMBNAIL) R.layout.feed_item else R.layout.feed_item_no_thumbnail
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return RedditFeedsViewHolder(view)
    }


    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> return LIST_VIEW_HEADER
            else -> {
                val feedDetail: FeedDetail = feeds!![position - 1]
                return if (hasThumbnailImage(feedDetail.large_image)) FEED_ITEM_WITH_THUMBNAIL else FEED_ITEM_NO_THUMBNAIL
            }
        }
    }

    class RedditFeedsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    class ListHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    private fun hasThumbnailImage(url: String?): Boolean {
        return url != null && url.contains("https")
    }
}


