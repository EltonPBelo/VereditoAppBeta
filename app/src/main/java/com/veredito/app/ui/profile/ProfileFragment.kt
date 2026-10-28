package com.veredito.app.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.veredito.app.R
import com.veredito.app.data.repository.VereditoRepository
import com.veredito.app.databinding.FragmentProfileBinding
import com.veredito.app.ui.feed.ReviewAdapter
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfileAvatar.load("https://i.pravatar.cc/150?img=45") { crossfade(true) }

        val adapter = ReviewAdapter(
            onCardClick = { review ->
                findNavController().navigate(R.id.detailFragment, bundleOf("reviewId" to review.id))
            },
            onLikeClick = { review -> VereditoRepository.toggleLike(review.id) }
        )

        binding.rvProfileReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfileReviews.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            VereditoRepository.profileName.collect { binding.tvProfileName.text = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            VereditoRepository.profileBio.collect { binding.tvProfileBio.text = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            VereditoRepository.reviews.collect { list ->
                adapter.submitList(list.take(2))
            }
        }

        binding.btnTabMyReviews.setOnClickListener {
            adapter.submitList(VereditoRepository.reviews.value.take(2))
        }
        binding.btnTabSaved.setOnClickListener {
            adapter.submitList(VereditoRepository.reviews.value.reversed().take(2))
        }

        binding.btnEditProfile.setOnClickListener { showEditProfileDialog() }

        binding.btnShareProfile.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Conheça ${binding.tvProfileName.text} (@marinacosta) no app Veredito!")
            }
            startActivity(Intent.createChooser(shareIntent, "Compartilhar Perfil"))
        }
    }

    private fun showEditProfileDialog() {
        val context = requireContext()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val etName = EditText(context).apply {
            hint = "Nome"
            setText(VereditoRepository.profileName.value)
        }
        val etBio = EditText(context).apply {
            hint = "Biografia"
            setText(VereditoRepository.profileBio.value)
        }
        container.addView(etName)
        container.addView(etBio)

        AlertDialog.Builder(context)
            .setTitle("Editar Perfil")
            .setView(container)
            .setPositiveButton("Salvar") { _, _ ->
                VereditoRepository.updateProfile(etName.text.toString(), etBio.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
