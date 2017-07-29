package com.example.android.littlereaderforreddit.UI

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.littlereaderforreddit.Data.CommentDetail
import com.example.android.littlereaderforreddit.Data.Comments
import com.example.android.littlereaderforreddit.Data.CommentsChildren
import com.example.android.littlereaderforreddit.R
import kotlinx.android.synthetic.main.comment_item.view.*
import java.util.*


class CommentsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var commentDetails: List<CommentDetail>? = null
    private val PADDING_UNIT = 30

    override fun getItemCount(): Int {
        return commentDetails?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        val padding = PADDING_UNIT * (viewType + 1)
        view.setPadding(padding, 0, PADDING_UNIT, 0)
        return CommentsViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val commentDetail = commentDetails!![position]
        return commentDetail.depth
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentDetail = commentDetails!![position]
        holder.itemView.user_name.text = commentDetail.author
        holder.itemView.created_time.text = commentDetail.created.toString()
        holder.itemView.comment_content.text = commentDetail.body
        holder.itemView.score.text = commentDetail.score.toString()
    }

    class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    fun setComments(comments: Comments) {
        val newCommentDetails = ArrayList<CommentDetail>()
        val stack = Stack<CommentDetail>()
        val commentChildrenList = comments.data.children
        for (i in (commentChildrenList.size - 1) downTo 0) {
            val child = commentChildrenList[i]
            if (child is CommentsChildren) {
                stack.push(child.data)
            }
        }
        while (!stack.isEmpty()) {
            val commentDetail = stack.pop()
            newCommentDetails.add(commentDetail)
            if (commentDetail.replies is Comments) {
                val replyComments = (commentDetail.replies).data.children
                (replyComments.size - 1 downTo 0)
                        .map { replyComments[it] }
                        .filterIsInstance<CommentsChildren>()
                        .forEach { stack.push(it.data) }
            }
        }
        commentDetails = newCommentDetails
        notifyDataSetChanged()
    }
}