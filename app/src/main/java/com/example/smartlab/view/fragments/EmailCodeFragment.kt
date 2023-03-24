package com.example.smartlab.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentEmailCodeBinding
import com.example.smartlab.utils.GenericKeyEvent
import com.example.smartlab.utils.GenericTextWatcher
import com.example.smartlab.utils.SaveStatus
import com.example.smartlab.viewmodel.EmailCodeViewModel

class EmailCodeFragment : Fragment() {

    private val binding: FragmentEmailCodeBinding by lazy {
        FragmentEmailCodeBinding.inflate(layoutInflater)
    }
    private val viewModel: EmailCodeViewModel by viewModels()


    private val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvCountDown.text =
                "Отправить код повторно можно\nбудет через ${millisUntilFinished / 1000} секунд"
        }

        override fun onFinish() {
            this.cancel()
            this.start()
            viewModel.sendCode(viewModel.email.value ?: "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpEditors()
        timer.start()
        applyClicks()
        viewModel.getEmail()
        setObservers()
    }

    private fun applyClicks() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        viewModel.signInStatus.observe(viewLifecycleOwner) {
            if (it == SaveStatus.SUCCESS) {
                timer.cancel()
                viewModel.clearSignInStatus()
                findNavController().navigate(R.id.action_emailCodeFragment_to_passwordFragment)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpEditors() {
        with(binding) {
            etCode1.addTextChangedListener(GenericTextWatcher(etCode1, etCode2))
            etCode2.addTextChangedListener(GenericTextWatcher(etCode2, etCode3))
            etCode3.addTextChangedListener(GenericTextWatcher(etCode3, etCode4))
            etCode4.addTextChangedListener(GenericTextWatcher(
                etCode4,
                null,
                onLastEditTextFilled = {
                    etCode4.clearFocus()
                    sendRequest()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    view?.let {
                        imm.hideSoftInputFromWindow(it.windowToken, 0)
                    }

                }
            ))

            etCode1.setOnKeyListener(GenericKeyEvent(etCode1, null))
            etCode2.setOnKeyListener(GenericKeyEvent(etCode2, etCode1))
            etCode3.setOnKeyListener(GenericKeyEvent(etCode3, etCode2))
            etCode4.setOnKeyListener(GenericKeyEvent(etCode4, etCode3))
        }
    }

    private fun sendRequest() {
        with(binding) {
            val password = "${etCode1.text}${etCode2.text}${etCode3.text}${etCode4.text}"
            viewModel.signIn(viewModel.email.value ?: "", password)
        }
    }
}