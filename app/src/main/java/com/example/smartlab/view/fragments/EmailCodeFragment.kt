package com.example.smartlab.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentEmailCodeBinding
import com.example.smartlab.utils.GenericKeyEvent
import com.example.smartlab.utils.GenericTextWatcher

class EmailCodeFragment : Fragment() {

    private val binding: FragmentEmailCodeBinding by lazy {
        FragmentEmailCodeBinding.inflate(layoutInflater)
    }

    private var requestingIsLocked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpEditors()
        initCountDownTimer()
        applyClicks()
    }

    private fun applyClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_emailCodeFragment_to_loginFragment)
        }
    }

    private fun setUpEditors() {
        with(binding) {
            //et1.requestFocus()

            et1.addTextChangedListener(GenericTextWatcher(et1, et2))
            et2.addTextChangedListener(GenericTextWatcher(et2, et3))
            et3.addTextChangedListener(GenericTextWatcher(et3, et4))
            et4.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s!!.isNotBlank()) {
                        // sending "request"
                        sendRequest()
                        //et4.clearFocus()
                        // locking attempt to request when 4-th digit is chosen
                        requestingIsLocked = true
                        findNavController().navigate(R.id.action_emailCodeFragment_to_passwordFragment)
                        timer.cancel()

                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            et1.setOnKeyListener(GenericKeyEvent(et1, null))
            et2.setOnKeyListener(GenericKeyEvent(et2, et1))
            et3.setOnKeyListener(GenericKeyEvent(et3, et2))
            et4.setOnKeyListener(GenericKeyEvent(et4, et3))
        }
    }

    private fun initCountDownTimer() {
        timer.start()
    }

    private fun sendRequest() {
        if (!requestingIsLocked) {
            with(binding) {
                if (editorsNotBlank()) {
                    val password = "${et1.text}${et2.text}${et3.text}${et4.text}"
                    // "requesting" api with code, navigating next
                    Toast.makeText(requireContext(),
                        "requesting api with code:$password",
                        Toast.LENGTH_SHORT).show()
                }
//                } else {
//                    Toast.makeText(requireContext(), "not filled", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    private fun editorsNotBlank(): Boolean {
        with(binding) {
            return et1.text.toString().isNotEmpty() && et2.text.toString().isNotEmpty()
                    && et3.text.toString().isNotEmpty() && et4.text.toString().isNotEmpty()
        }
    }

    private val timer = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvCountDown.text =
                "Отправить код повторно можно\nбудет через ${millisUntilFinished / 1000} секунд"
        }

        override fun onFinish() {
            // start timer one more time on finish
            this.start()
            // unlocking requesting
            requestingIsLocked = false
            // sending "request"
            sendRequest()
            // locking again
            requestingIsLocked = true
        }
    }
}