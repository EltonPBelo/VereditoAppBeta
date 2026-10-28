package com.veredito.app.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.veredito.app.R
import com.veredito.app.databinding.FragmentFeedBinding
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReviewAdapter(
            onCardClick = { review ->
                findNavController().navigate(R.id.detailFragment, bundleOf("reviewId" to review.id))
            },
            onLikeClick = { review ->
                viewModel.toggleLike(review.id)
            }
        )

        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = adapter

        setupCategoryChips()

        binding.btnTopSearch.setOnClickListener { findNavController().navigate(R.id.searchFragment) }
        binding.btnTopNotif.setOnClickListener { findNavController().navigate(R.id.notificationsFragment) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredReviews.collect { list ->
                    adapter.submitList(list)
                }
            }
        }
    }

    private fun setupCategoryChips() {
        binding.cgCategories.removeAllViews()
        viewModel.categories.forEachIndexed { index, category ->
            val chip = Chip(requireContext()).apply {
                text = category
                isCheckable = true
                isChecked = index == 0
                setOnClickListener { viewModel.selectCategory(category) }
            }
            binding.cgCategories.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
