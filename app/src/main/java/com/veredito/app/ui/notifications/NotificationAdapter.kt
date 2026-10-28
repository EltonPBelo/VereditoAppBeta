package com.veredito.app.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.veredito.app.data.model.NotificationItem
import com.veredito.app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onClick: (NotificationItem) -> Unit
) : ListAdapter<NotificationItem, NotificationAdapter.NotifViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(oldItem: NotificationItem, newItem: NotificationItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: NotificationItem, newItem: NotificationItem) = oldItem == newItem
    }

    inner class NotifViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationItem) = with(binding) {
            viewUnreadDot.visibility = if (item.read) View.INVISIBLE else View.VISIBLE
            tvNotifIcon.text = item.type.icon
            val prefix = if (item.actor != null) "${item.actor} " else ""
            tvNotifBody.text = "$prefix${item.body}"
            tvNotifTime.text = "há ${item.time}"
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
