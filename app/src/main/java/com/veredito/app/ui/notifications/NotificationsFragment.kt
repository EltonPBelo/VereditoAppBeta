package com.veredito.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.veredito.app.R
import com.veredito.app.data.repository.VereditoRepository
import com.veredito.app.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var onlyUnread = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NotificationAdapter { notif ->
            VereditoRepository.markNotificationRead(notif.id)
            if (notif.productName != null) {
                val review = VereditoRepository.reviews.value.find { it.product == notif.productName }
                if (review != null) {
                    findNavController().navigate(R.id.detailFragment, bundleOf("reviewId" to review.id))
                }
            }
        }

        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        binding.btnMarkAllRead.setOnClickListener {
            VereditoRepository.markAllNotificationsRead()
        }

        binding.btnNotifAll.setOnClickListener {
            onlyUnread = false
            refreshList(adapter)
        }

        binding.btnNotifUnread.setOnClickListener {
            onlyUnread = true
            refreshList(adapter)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            VereditoRepository.notifications.collect {
                refreshList(adapter)
            }
        }
    }

    private fun refreshList(adapter: NotificationAdapter) {
        val all = VereditoRepository.notifications.value
        val unreadCount = all.count { !it.read }
        binding.btnNotifUnread.text = "Não lidos ($unreadCount)"
        adapter.submitList(if (onlyUnread) all.filter { !it.read } else all)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
