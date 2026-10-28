package com.veredito.app.data.model

data class Comment(
    val id: Int,
    val user: String,
    val avatar: String,
    val text: String,
    val likes: Int,
    val time: String,
    val isOwner: Boolean
)

data class Review(
    val id: Int,
    val product: String,
    val category: String,
    val brand: String,
    val reviewer: String,
    val reviewerAvatar: String,
    val verified: Boolean,
    val time: String,
    val rating: Int,
    val ratingLabel: String,
    val headline: String,
    val body: String,
    val pros: List<String>,
    val cons: List<String>,
    val images: List<String>,
    val likes: Int,
    val isLiked: Boolean = false,
    val comments: List<Comment>
)

enum class NotifType(val icon: String) {
    LIKE("❤️"), COMMENT("💬"), REPLY("↩️"), FOLLOW("👤"), MENTION("@"), SYSTEM("🏆"), TRENDING("🔥")
}

data class NotificationItem(
    val id: Int,
    val type: NotifType,
    val read: Boolean,
    val time: String,
    val actor: String? = null,
    val productName: String? = null,
    val body: String
)

data class SearchFilters(
    val minRating: Int = 0,
    val onlyVerified: Boolean = false
)
