package com.veredito.app.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.veredito.app.R
import com.veredito.app.data.model.Comment
import com.veredito.app.databinding.ItemCommentBinding

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) = with(binding) {
            ivCommentAvatar.load(item.avatar) { crossfade(true) }
            tvCommentUser.text = item.user
            tvCommentText.text = item.text
            tvCommentMeta.text = "♥ ${item.likes}   •   ${item.time} atrás"

            if (item.isOwner) {
                tvCommentRole.text = "PROPRIETÁRIO"
                tvCommentRole.setTextColor(ContextCompat.getColor(root.context, R.color.owner_green))
                tvCommentRole.setBackgroundResource(R.drawable.bg_box_owner)
            } else {
                tvCommentRole.text = "INTERESSADO"
                tvCommentRole.setTextColor(ContextCompat.getColor(root.context, R.color.non_owner_blue))
                tvCommentRole.setBackgroundResource(R.drawable.bg_box_non_owner)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
