package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentOnboardingBinding
import com.example.smartlab.utils.SwipeControlTouchListener
import com.example.smartlab.utils.SwipeDirection
import com.example.smartlab.view.adapters.OnboardingAdapter
import com.example.smartlab.viewmodel.OnboardingViewModel

class OnboardingFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnBoardingViewPager()
        setObservers()
        setListeners()
    }

    private fun setObservers(){
        viewModel.buttonText.observe(viewLifecycleOwner) {
            binding.tvOnboarding.text = it
        }
    }

    private fun setListeners() {
        binding.tvOnboarding.setOnClickListener {
            viewModel.navigateToLoginScreen()
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
        }
    }

    private fun initOnBoardingViewPager() {
        val onBoardingAdapter = OnboardingAdapter(requireActivity())
        binding.vpOnboarding.adapter = onBoardingAdapter
        onBoardingAdapter.updatePages(viewModel.onboardingItems)
        (binding.vpOnboarding[0] as RecyclerView).addOnItemTouchListener(SwipeControlTouchListener().apply {
            setSwipeDirection(
                SwipeDirection.RIGHT
            )
        })
        binding.vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(viewModel.scrollCount == 2){
                    binding.vpOnboarding.isUserInputEnabled = false
                    viewModel.onboardingItems.removeFirst()
                    viewModel.isLastScreen.value = true
                    viewModel.nextPage()
                }
                when (position) {
                    0 -> {
                        binding.indicator1.setImageResource(R.drawable.selected_indicator)
                        binding.indicator2.setImageResource(R.drawable.unselected_indicator)
                        binding.indicator3.setImageResource(R.drawable.unselected_indicator)
                    }
                    1 -> {
                        binding.indicator1.setImageResource(R.drawable.unselected_indicator)
                        binding.indicator2.setImageResource(R.drawable.selected_indicator)
                        binding.indicator3.setImageResource(R.drawable.unselected_indicator)
                        viewModel.nextPage()
                        onBoardingAdapter.updatePages(viewModel.onboardingItems)
                    }
                    2 -> {
                        binding.indicator1.setImageResource(R.drawable.unselected_indicator)
                        binding.indicator2.setImageResource(R.drawable.unselected_indicator)
                        binding.indicator3.setImageResource(R.drawable.selected_indicator)
                        viewModel.nextPage()
                        onBoardingAdapter.updatePages(viewModel.onboardingItems)
                    }
                }
            }
        })
        (binding.vpOnboarding[0] as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }
}