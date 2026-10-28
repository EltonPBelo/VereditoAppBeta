package com.veredito.app.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.veredito.app.R
import com.veredito.app.data.model.Review
import com.veredito.app.databinding.ItemReviewCardBinding

class ReviewAdapter(
    private val onCardClick: (Review) -> Unit,
    private val onLikeClick: (Review) -> Unit
) : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review) = oldItem == newItem
    }

    inner class ReviewViewHolder(private val binding: ItemReviewCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) = with(binding) {
            ivAvatar.load(item.reviewerAvatar) { crossfade(true) }
            tvReviewerName.text = item.reviewer
            tvVerifiedBadge.visibility = if (item.verified) View.VISIBLE else View.GONE
            tvTime.text = item.time
            tvCategoryBadge.text = item.category

            ivProductImage.load(item.images.firstOrNull()) { crossfade(true) }
            if (item.images.size > 1) {
                tvMorePhotos.visibility = View.VISIBLE
                tvMorePhotos.text = "+${item.images.size - 1} fotos"
            } else {
                tvMorePhotos.visibility = View.GONE
            }

            tvProductName.text = item.product
            tvBrand.text = item.brand
            val stars = "★".repeat(item.rating) + "☆".repeat(5 - item.rating)
            tvRatingStars.text = "$stars  ${item.ratingLabel}"
            tvHeadline.text = "\"${item.headline}\""

            val ownerCount = item.comments.count { it.isOwner }
            val nonOwnerCount = item.comments.count { !it.isOwner }
            tvOwnerCount.text = "$ownerCount comentários"
            tvNonOwnerCount.text = "$nonOwnerCount perguntas"

            btnLike.text = "♥ ${item.likes}"
            btnLike.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (item.isLiked) R.color.primary_glow else R.color.text_muted
                )
            )
            btnCommentsCount.text = "💬 ${item.comments.size}"

            btnLike.setOnClickListener { onLikeClick(item) }
            ivProductImage.setOnClickListener { onCardClick(item) }
            btnOpenDetail.setOnClickListener { onCardClick(item) }
            root.setOnClickListener { onCardClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
