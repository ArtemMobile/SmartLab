package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartlab.databinding.FragmentLoginBinding

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


    }

//    private fun setUpEditors(){
//        with(binding){
//            et1.addTextChangedListener(GenericTextWatcher(et1, et2))
//            et2.addTextChangedListener(GenericTextWatcher(et2, et3))
//            et3.addTextChangedListener(GenericTextWatcher(et3, et4))
//            et4.addTextChangedListener(object: TextWatcher{
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int,
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if(s.toString().isNotBlank()){
//                        binding.et4.setBackgroundDrawable(resources.getDrawable(R.drawable.single_digit_editor_filled))
//
//                    }
//                    else{
//                        binding.et4.setBackgroundDrawable(resources.getDrawable(R.drawable.single_digit_editor))
//                    }
//                    val password = "${binding.et1.text}${binding.et2.text}${binding.et3.text}${binding.et4.text}"
//                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//
//                }
//
//            })
//
//            et1.setOnKeyListener(GenericKeyEvent(et1, null))
//            et2.setOnKeyListener(GenericKeyEvent(et2, et1))
//            et3.setOnKeyListener(GenericKeyEvent(et3, et2))
//            et4.setOnKeyListener(GenericKeyEvent(et4,et3))
//        }
//    }
}