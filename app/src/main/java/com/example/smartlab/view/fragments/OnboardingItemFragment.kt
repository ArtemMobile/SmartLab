package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.smartlab.databinding.FragmentItemOnboardingBinding
import com.example.smartlab.viewmodel.OnboardingViewModel

class OnboardingItemFragment() : Fragment() {

    private val viewModel: OnboardingViewModel by viewModels()

    private val binding: FragmentItemOnboardingBinding by lazy {
        FragmentItemOnboardingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = viewModel.onboardingItems[requireArguments().getInt(PAGE_POSITION, 0)]
        with(binding) {
            tvOnboardingTitle.text = item.title
            tvOnboardingDescription.text = item.description
            ivOnboardingImage.setImageResource(item.imageRes)
        }
    }

    companion object {
        val PAGE_POSITION = "PAGE_POSITION"
        fun newInstance(pagePosition: Int): OnboardingItemFragment {
            return OnboardingItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_POSITION, pagePosition)
                }
            }
        }
    }
}