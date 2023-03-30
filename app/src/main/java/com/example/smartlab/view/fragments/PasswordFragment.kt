package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentPasswordBinding
import com.example.smartlab.utils.DataStore

class PasswordFragment : Fragment() {

    private val binding: FragmentPasswordBinding by lazy {
        FragmentPasswordBinding.inflate(layoutInflater)
    }

    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyClicks()
    }

    private fun applyClicks() {
        with(binding) {
            grid.iterator().forEach { view ->
                if (view is AppCompatButton) {
                    (view).setOnClickListener {
                        setPassword(it)
                    }
                }
            }
            btnClear.setOnClickListener { clearOneDigit() }
            tvSkip.setOnClickListener { findNavController().navigate(R.id.action_passwordFragment_to_patientCardFragment) }
        }
    }

    private fun setPassword(button: View) {
        applyDots()
        if (password.length < 4) {
            password += (button as AppCompatButton).text
            // just to display current password
        }
        if (password.length == 4) {
            // navigating next here + saving password securely
            DataStore.encryptPassword(password)
            DataStore.savePasswordCreated(requireContext())
            findNavController().navigate(R.id.action_passwordFragment_to_patientCardFragment)
        }
    }

    private fun clearOneDigit() {
        if (password.isNotEmpty()) {
            password = password.dropLast(1)
            applyDots()
            // just to prove that last digit is getting deleted
        }
    }

    private fun applyDots() {
        when (password.length) {
            0 -> {
                binding.indicator1.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator2.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator3.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator4.setImageResource(R.drawable.empty_password_indicator)
            }
            1 -> {
                binding.indicator1.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator2.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator3.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator4.setImageResource(R.drawable.empty_password_indicator)
            }
            2 -> {
                binding.indicator1.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator2.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator3.setImageResource(R.drawable.empty_password_indicator)
                binding.indicator4.setImageResource(R.drawable.empty_password_indicator)
            }
            3 -> {
                binding.indicator1.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator2.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator3.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator4.setImageResource(R.drawable.empty_password_indicator)
            }
            4 -> {
                binding.indicator1.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator2.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator3.setImageResource(R.drawable.filled_password_indicator)
                binding.indicator4.setImageResource(R.drawable.filled_password_indicator)
            }
        }
    }
}