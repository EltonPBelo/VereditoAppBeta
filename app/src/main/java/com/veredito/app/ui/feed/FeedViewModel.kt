package com.veredito.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veredito.app.data.model.Review
import com.veredito.app.data.repository.VereditoRepository
import kotlinx.coroutines.flow.*

class FeedViewModel : ViewModel() {

    private val selectedCategory = MutableStateFlow("Todos")

    val categories = listOf("Todos", "Eletrônicos", "Smartphones", "Audio", "Casa", "Moda", "Games")

    val filteredReviews: StateFlow<List<Review>> = combine(
        VereditoRepository.reviews,
        selectedCategory
    ) { reviews, category ->
        if (category == "Todos") reviews
        else reviews.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        selectedCategory.value = category
    }

    fun toggleLike(reviewId: Int) {
        VereditoRepository.toggleLike(reviewId)
    }
}
