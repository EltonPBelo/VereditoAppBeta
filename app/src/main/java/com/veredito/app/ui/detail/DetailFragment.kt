package com.veredito.app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.veredito.app.R
import com.veredito.app.data.model.Review
import com.veredito.app.data.repository.VereditoRepository
import com.veredito.app.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var reviewId: Int = 1
    private var activeImageIndex = 0
    private var showingOwnersTab = true
    private val commentAdapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewId = arguments?.getInt("reviewId") ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = commentAdapter

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                VereditoRepository.reviews.collect { list ->
                    val review = list.find { it.id == reviewId } ?: list.first()
                    renderReview(review)
                }
            }
        }
    }

    private fun renderReview(review: Review): Unit = with(binding) {
        tvDetailTitle.text = review.product
        tvDetailBrand.text = review.brand

        val safeIndex = activeImageIndex % review.images.size
        ivDetailHero.load(review.images[safeIndex]) { crossfade(true) }
        tvImageIndicator.text = "Foto ${safeIndex + 1}/${review.images.size} (Toque para alternar)"

        ivDetailHero.setOnClickListener {
            activeImageIndex = (activeImageIndex + 1) % review.images.size
            renderReview(review)
        }

        ivDetailAvatar.load(review.reviewerAvatar)
        tvDetailReviewer.text = review.reviewer
        tvDetailTime.text = "${review.time} • Comprador Verificado"
        tvDetailScore.text = "${review.rating}.0"
        tvDetailRatingLabel.text = review.ratingLabel.uppercase()
        tvDetailHeadline.text = "\"${review.headline}\""
        tvDetailBody.text = review.body

        tvProsList.text = review.pros.joinToString("\n") { "✓ $it" }
        tvConsList.text = review.cons.joinToString("\n") { "✗ $it" }

        val ownerComments = review.comments.filter { it.isOwner }
        val nonOwnerComments = review.comments.filter { !it.isOwner }

        btnTabOwners.text = "✅ Tenho (${ownerComments.size})"
        btnTabNonOwners.text = "💬 Não tenho (${nonOwnerComments.size})"

        updateCommentsTabUI(ownerComments, nonOwnerComments)

        btnTabOwners.setOnClickListener {
            showingOwnersTab = true
            updateCommentsTabUI(ownerComments, nonOwnerComments)
        }

        btnTabNonOwners.setOnClickListener {
            showingOwnersTab = false
            updateCommentsTabUI(ownerComments, nonOwnerComments)
        }

        btnSendComment.setOnClickListener {
            val text = etNewComment.text.toString()
            if (text.isNotBlank()) {
                VereditoRepository.addComment(review.id, text, showingOwnersTab)
                etNewComment.text.clear()
                Toast.makeText(requireContext(), "Comentário publicado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCommentsTabUI(
        ownerComments: List<com.veredito.app.data.model.Comment>,
        nonOwnerComments: List<com.veredito.app.data.model.Comment>
    ) = with(binding) {
        if (showingOwnersTab) {
            commentAdapter.submitList(ownerComments)
            tvTabHint.text = "✅ Opiniões de quem já possui o produto e pode confirmar a experiência real."
            tvTabHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.owner_green))
            tvTabHint.setBackgroundResource(R.drawable.bg_box_owner)
            etNewComment.hint = "Compartilhe sua experiência como proprietário..."
        } else {
            commentAdapter.submitList(nonOwnerComments)
            tvTabHint.text = "💬 Perguntas e comentários de quem ainda não possui o produto."
            tvTabHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.non_owner_blue))
            tvTabHint.setBackgroundResource(R.drawable.bg_box_non_owner)
            etNewComment.hint = "Qual é a sua dúvida sobre esse produto?"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
