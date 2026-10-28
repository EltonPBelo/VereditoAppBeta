package com.veredito.app.data.repository

import com.veredito.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object VereditoRepository {

    private val _reviews = MutableStateFlow(initialReviews())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _notifications = MutableStateFlow(initialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _profileName = MutableStateFlow("Marina Costa")
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    private val _profileBio = MutableStateFlow("Tecnologia no dia a dia, sem complicação. Testo de verdade antes de dar meu veredito.")
    val profileBio: StateFlow<String> = _profileBio.asStateFlow()

    fun toggleLike(reviewId: Int) {
        _reviews.update { list ->
            list.map { r ->
                if (r.id == reviewId) {
                    val nowLiked = !r.isLiked
                    r.copy(isLiked = nowLiked, likes = if (nowLiked) r.likes + 1 else r.likes - 1)
                } else r
            }
        }
    }

    fun addComment(reviewId: Int, text: String, isOwner: Boolean) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = System.currentTimeMillis().toInt(),
            user = _profileName.value,
            avatar = "https://i.pravatar.cc/150?img=45",
            text = text.trim(),
            likes = 0,
            time = "agora",
            isOwner = isOwner
        )
        _reviews.update { list ->
            list.map { r ->
                if (r.id == reviewId) r.copy(comments = r.comments + newComment) else r
            }
        }
    }

    fun addReview(product: String, brand: String, category: String, rating: Int, headline: String, body: String) {
        val labels = listOf("", "Ruim", "Regular", "Bom", "Muito Bom", "Excepcional")
        val newReview = Review(
            id = (_reviews.value.maxOfOrNull { it.id } ?: 0) + 1,
            product = product.ifBlank { "Produto Avaliado" },
            category = category.ifBlank { "Eletrônicos" },
            brand = brand.ifBlank { "Marca" },
            reviewer = _profileName.value,
            reviewerAvatar = "https://i.pravatar.cc/150?img=45",
            verified = true,
            time = "agora",
            rating = rating.coerceIn(1, 5),
            ratingLabel = labels[rating.coerceIn(1, 5)],
            headline = headline.ifBlank { "Excelente experiência com o produto!" },
            body = body.ifBlank { "Produto testado e aprovado no uso diário." },
            pros = listOf("Ótimo acabamento", "Cumpre o que promete"),
            cons = listOf("Preço poderia ser menor"),
            images = listOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=500&fit=crop&auto=format"),
            likes = 1,
            comments = emptyList()
        )
        _reviews.update { listOf(newReview) + it }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(read = true) } }
    }

    fun markNotificationRead(id: Int) {
        _notifications.update { list -> list.map { if (it.id == id) it.copy(read = true) else it } }
    }

    fun updateProfile(name: String, bio: String) {
        if (name.isNotBlank()) _profileName.value = name
        if (bio.isNotBlank()) _profileBio.value = bio
    }

    private fun initialReviews() = listOf(
        Review(
            id = 1,
            product = "Sony WH-1000XM5",
            category = "Audio",
            brand = "Sony",
            reviewer = "Rafael Mendes",
            reviewerAvatar = "https://i.pravatar.cc/150?img=12",
            verified = true,
            time = "2h atrás",
            rating = 5,
            ratingLabel = "Excepcional",
            headline = "O melhor cancelamento de ruído que já usei na vida",
            body = "Comprei depois de muito pesquisar e posso dizer que foi a melhor decisão. O cancelamento de ruído é surreal — literalmente não escuto nada ao redor. A qualidade do áudio é cristalina nos agudos e potente nos graves sem exagero. Uso no metrô todo dia e a bateria dura fácil dois dias.",
            pros = listOf("Cancelamento de ruído incrível", "Qualidade de áudio audiófilo", "Bateria de 30h", "App com equalização"),
            cons = listOf("Levemente desconfortável após 4h", "Case grande demais para a mochila"),
            images = listOf(
                "https://images.unsplash.com/photo-1515940175183-6798529cb860?w=600&h=500&fit=crop&auto=format"
            ),
            likes = 347,
            comments = listOf(
                Comment(1, "Luciana Torres", "https://i.pravatar.cc/150?img=5", "Tenho há 8 meses e confirmo tudo! O ANC realmente some com barulho de avião.", 42, "1h", true),
                Comment(2, "Bruno Alves", "https://i.pravatar.cc/150?img=15", "Comprei na semana passada e a diferença para o meu antigo JBL é gritante.", 28, "30min", true)
            )
        ),
        Review(
            id = 2,
            product = "iPhone 15 Pro Max",
            category = "Smartphones",
            brand = "Apple",
            reviewer = "Fernanda Lima",
            reviewerAvatar = "https://i.pravatar.cc/150?img=25",
            verified = true,
            time = "5h atrás",
            rating = 4,
            ratingLabel = "Muito Bom",
            headline = "Câmera revolucionária, mas bateria ainda deixa a desejar",
            body = "Virei do Android depois de 10 anos e estou impressionada com a integração do ecossistema Apple. A câmera periscópica com 5x de zoom óptico mudou como faço fotos. O chip A17 Pro deixa qualquer coisa fluida.",
            pros = listOf("Câmera periscópica 5x revolucionária", "Desempenho do chip A17 Pro", "Tela Dynamic Island muito útil"),
            cons = listOf("Bateria não dura dia inteiro com uso intenso", "Preço elevado", "Aquece em gravação 4K"),
            images = listOf(
                "https://images.unsplash.com/photo-1620783770629-122b7f187703?w=600&h=500&fit=crop&auto=format"
            ),
            likes = 512,
            comments = listOf(
                Comment(5, "Marco Vieira", "https://i.pravatar.cc/150?img=33", "Tenho o 15 Pro e a bateria aqui aguenta bem melhor que o meu 13.", 31, "4h", true)
            )
        )
    )

    private fun initialNotifications() = listOf(
        NotificationItem(1, NotifType.LIKE, false, "2min", "Luciana Torres", "Sony WH-1000XM5", "curtiu seu review do Sony WH-1000XM5"),
        NotificationItem(2, NotifType.COMMENT, false, "8min", "Bruno Alves", "Sony WH-1000XM5", "comentou no seu review do Sony WH-1000XM5")
    )
}
