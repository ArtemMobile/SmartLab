package com.example.smartlab.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentLoginBinding
import com.example.smartlab.utils.SendCodeStatus
import com.example.smartlab.utils.Utils
import com.example.smartlab.viewmodel.LoginViewModel
import com.example.smartlab.viewmodel.ProfileViewModel

class LoginFragment : Fragment() {

    private val binding: FragmentLoginBinding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private val TAG = this::class.simpleName
    private val profileViewModel: ProfileViewModel by viewModels()

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
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        binding.btnNext.setOnClickListener {
            Log.d(TAG, "setListeners: Enabled: ${it.isEnabled}")
            viewModel.sendCode(binding.etEmail.text.toString())
        }
    }
    private fun setObservers(){
        viewModel.sendCodeStatus.observe(viewLifecycleOwner) {
            when (it) {
                SendCodeStatus.SUCCESS -> {
                    viewModel.saveEmail(binding.etEmail.text.toString())
                    findNavController().navigate(R.id.action_loginFragment_to_emailCodeFragment)
                    viewModel.clearSendCodeStatus()
                }
                SendCodeStatus.FAIL -> {
                    Toast.makeText(requireContext(), "Ошибка при отправке кода", Toast.LENGTH_SHORT).show()
                }
                SendCodeStatus.NOTHING -> {}
            }
        }
        viewModel.saveEmailStatus.observe(viewLifecycleOwner) {
            Log.d(TAG, "setObservers: SaveEmailStatus - ${it.name}")
        }
    }


    private fun applyButtonNext() {
        binding.btnNext.apply {
            setBackgroundColor(resources.getColor(R.color.inactive_button, null))
            isEnabled = false
        }
        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            if (Utils.isEmailValid(text!!.toString())) {
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