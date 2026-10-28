package com.veredito.app.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.veredito.app.R
import com.veredito.app.data.model.SearchFilters
import com.veredito.app.data.repository.VereditoRepository
import com.veredito.app.databinding.FragmentSearchBinding
import com.veredito.app.ui.feed.ReviewAdapter
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var filters = SearchFilters()
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReviewAdapter(
            onCardClick = { review ->
                findNavController().navigate(R.id.detailFragment, bundleOf("reviewId" to review.id))
            },
            onLikeClick = { review -> VereditoRepository.toggleLike(review.id) }
        )

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = adapter

        binding.etSearch.doAfterTextChanged { performSearch() }

        binding.btnOpenFilters.setOnClickListener {
            FilterBottomSheet(filters) { newFilters ->
                filters = newFilters
                binding.btnOpenFilters.text = if (filters.minRating > 0) "Filtros (${filters.minRating}★)" else "Filtros"
                performSearch()
            }.show(parentFragmentManager, "FilterBottomSheet")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            VereditoRepository.reviews.collect { performSearch() }
        }
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim().lowercase()
        val all = VereditoRepository.reviews.value

        val filtered = all.filter { review ->
            val matchesQuery = query.isEmpty() ||
                review.product.lowercase().contains(query) ||
                review.brand.lowercase().contains(query) ||
                review.category.lowercase().contains(query)
            val matchesRating = review.rating >= filters.minRating
            val matchesVerified = !filters.onlyVerified || review.verified
            matchesQuery && matchesRating && matchesVerified
        }
        adapter.submitList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
