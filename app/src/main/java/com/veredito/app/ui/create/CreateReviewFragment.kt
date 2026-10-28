package com.veredito.app.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.veredito.app.R
import com.veredito.app.data.repository.VereditoRepository
import com.veredito.app.databinding.FragmentCreateReviewBinding

class CreateReviewFragment : Fragment() {

    private var _binding: FragmentCreateReviewBinding? = null
    private val binding get() = _binding!!
    private var step = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStepUI()

        binding.btnNextStep.setOnClickListener {
            if (step < 3) {
                step++
                updateStepUI()
            } else {
                VereditoRepository.addReview(
                    product = binding.etProductName.text.toString(),
                    brand = binding.etProductBrand.text.toString(),
                    category = binding.etProductCategory.text.toString(),
                    rating = binding.ratingBarCreate.rating.toInt(),
                    headline = binding.etReviewHeadline.text.toString(),
                    body = binding.etReviewBody.text.toString()
                )
                Toast.makeText(requireContext(), "🚀 Review publicado com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.feedFragment)
            }
        }
    }

    private fun updateStepUI() = with(binding) {
        tvStepIndicator.text = "PUBLICAR REVIEW • PASSO $step DE 3"
        step1Container.visibility = if (step == 1) View.VISIBLE else View.GONE
        step2Container.visibility = if (step == 2) View.VISIBLE else View.GONE
        step3Container.visibility = if (step == 3) View.VISIBLE else View.GONE

        when (step) {
            1 -> {
                tvStepTitle.text = "Qual produto você está avaliando?"
                btnNextStep.text = "Continuar →"
            }
            2 -> {
                tvStepTitle.text = "Adicione fotos e vídeos"
                btnNextStep.text = "Continuar →"
            }
            3 -> {
                tvStepTitle.text = "Escreva sua avaliação"
                btnNextStep.text = "🚀 Publicar Review"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
