package com.veredito.app.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.veredito.app.R
import com.veredito.app.data.model.SearchFilters
import com.veredito.app.databinding.BottomSheetFiltersBinding

class FilterBottomSheet(
    private val currentFilters: SearchFilters,
    private val onApply: (SearchFilters) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFiltersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (currentFilters.minRating) {
            4 -> binding.rgMinRating.check(R.id.rb_4stars)
            5 -> binding.rgMinRating.check(R.id.rb_5stars)
            else -> binding.rgMinRating.check(R.id.rb_all)
        }
        binding.swVerifiedOnly.isChecked = currentFilters.onlyVerified

        binding.btnApplyFilters.setOnClickListener {
            val minRating = when (binding.rgMinRating.checkedRadioButtonId) {
                R.id.rb_4stars -> 4
                R.id.rb_5stars -> 5
                else -> 0
            }
            onApply(SearchFilters(minRating = minRating, onlyVerified = binding.swVerifiedOnly.isChecked))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
