package com.example.android.littlereaderforreddit.UI

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.R
import kotlinx.android.synthetic.main.subreddit_filter_item.view.*


class FilterAdapter(val subreddits: List<Subreddit>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val HEADER_VIEW_TYPE = 0
    private val SUBREDDIT_VIEW_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_list_header_item, parent, false)
            return HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.subreddit_filter_item, parent, false)
            return FilterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FilterViewHolder) {
            holder.itemView.subreddit_filter_name.text = subreddits!![position - 1].data.display_name
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER_VIEW_TYPE
            else -> SUBREDDIT_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return subreddits?.size ?: 0
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}