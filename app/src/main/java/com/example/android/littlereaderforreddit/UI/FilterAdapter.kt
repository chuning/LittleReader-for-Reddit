package com.example.android.littlereaderforreddit.UI

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.Subreddit
import com.example.android.littlereaderforreddit.R
import kotlinx.android.synthetic.main.subreddit_filter_item.view.*


class FilterAdapter(val subreddits: List<Subreddit>, var excludedSubreddits: MutableSet<Subreddit>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            val subreddit = subreddits[position - 1]
            holder.itemView.subreddit_filter_name.text = subreddit.display_name
            holder.itemView.checkbox.isChecked = !excludedSubreddits.contains(subreddit)
            holder.itemView.checkbox.setOnCheckedChangeListener {buttonView, isChecked ->
                 buttonView.isChecked = isChecked
                 if (isChecked) {
                     excludedSubreddits.remove(subreddit)
                 } else {
                     excludedSubreddits.add(subreddit)
                 }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER_VIEW_TYPE
            else -> SUBREDDIT_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return subreddits.size + 1
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

}