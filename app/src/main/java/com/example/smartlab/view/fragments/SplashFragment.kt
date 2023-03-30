package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.app.App
import com.example.smartlab.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(1500)
            (requireContext().applicationContext.applicationContext as App).isCreateProfilePassed.collect { isCreatePatientCardPassed ->
                if (isCreatePatientCardPassed) {
                    findNavController().navigate(R.id.action_splashFragment_to_mainActivity)
                    requireActivity().finish()
                } else {
                    (requireContext().applicationContext.applicationContext as App).isCreatePasswordPassed.collect { isPasswordCreated ->
                        if (!isPasswordCreated) {
                            findNavController().navigate(R.id.action_splashFragment_to_passwordFragment)
                        } else {
                            (requireContext().applicationContext.applicationContext as App).isOnboardingPassedFlow.collect { isOnboardingPassed ->
                                if (isOnboardingPassed) {
                                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                                } else {
                                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}