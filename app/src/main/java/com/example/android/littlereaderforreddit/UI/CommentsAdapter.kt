package com.example.android.littlereaderforreddit.UI

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.CommentDetail
import com.example.android.littlereaderforreddit.Data.Comments
import com.example.android.littlereaderforreddit.Data.CommentsChildren
import com.example.android.littlereaderforreddit.R
import com.example.android.littlereaderforreddit.Util.DateTimeUtil
import com.example.android.littlereaderforreddit.Util.StringFormatUtil
import kotlinx.android.synthetic.main.comment_item.view.*
import java.util.*


class CommentsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var commentDetails: List<CommentDetail>? = null
    private val PADDING_UNIT = 20

    override fun getItemCount(): Int {
        return commentDetails?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        val padding = PADDING_UNIT * viewType
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = padding
        return CommentsViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val commentDetail = commentDetails!![position]
        return commentDetail.depth
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentDetail = commentDetails!![position]
        holder.itemView.user_name.text = commentDetail.author
        holder.itemView.created_time.text = String.format("·%s", DateTimeUtil.deltaTime(commentDetail.created_utc!!))
        holder.itemView.score.text = commentDetail.score.toString()
        StringFormatUtil.formatHtml(commentDetail.body_html?:"", holder.itemView.comment_content)
    }

    class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    fun setComments(comments: Comments) {
        // in-order traverse using stack
        val newCommentDetails = ArrayList<CommentDetail>()
        val stack = Stack<CommentDetail>()
        val commentChildrenList = comments.data.children
        for (i in (commentChildrenList.size - 1) downTo 0) {
            val child = commentChildrenList[i]
            if (child is CommentsChildren && child.data != null) {
                stack.push(child.data)
            }
        }
        while (!stack.isEmpty()) {
            val commentDetail = stack.pop()
            newCommentDetails.add(commentDetail)
            if (commentDetail.replies != null) {
                val replyComments = (commentDetail.replies).data.children
                (replyComments.size - 1 downTo 0)
                        .map { replyComments[it] }
                        .filterIsInstance<CommentsChildren>()
                        .filter { it.data != null }
                        .forEach { stack.push(it.data) }
            }
        }
        commentDetails = newCommentDetails
        notifyDataSetChanged()
    }
}