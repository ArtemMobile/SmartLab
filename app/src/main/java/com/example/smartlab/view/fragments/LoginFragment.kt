package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentLoginBinding
import java.util.regex.Pattern

class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyButtonNext()
    }

    private fun isEmailValid(email: String): Boolean {
        val emailValidator = Pattern.compile(
            "[a-z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-z0-9][a-z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-z0-9][a-z0-9\\-]{0,25}" +
                    ")+"
        )
        return emailValidator.matcher(email).matches()
    }

    private fun applyButtonNext() {

        binding.btnNext.apply {
            setBackgroundColor(resources.getColor(R.color.inactive_button, null))
            isEnabled = false
            setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_emailCodeFragment)

            }
        }
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            if (isEmailValid(text!!.toString())) {

                binding.btnNext.apply {
                    setBackgroundColor(resources.getColor(R.color.blue_button, null))
                    isEnabled = true
                }
            } else {
                binding.btnNext.apply {
                    setBackgroundColor(resources.getColor(R.color.inactive_button, null))
                    isEnabled = false
                }
            }
        }
    }

}