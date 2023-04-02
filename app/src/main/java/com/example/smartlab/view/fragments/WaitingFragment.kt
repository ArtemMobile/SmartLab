package com.example.smartlab.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentWaitingBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class WaitingFragment : Fragment() {

    private val binding: FragmentWaitingBinding by lazy{
        FragmentWaitingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWaitingScreen()
        applyClicks()
    }

    private fun initWaitingScreen(){
        lifecycleScope.launch {
            binding.waitingContainer.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            delay(3000)
            binding.tvWaiting.text = "Производим оплату..."
            delay(2000)
            binding.waitingContainer.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
        }
    }

    private fun applyClicks(){
        binding.btnGoMain.setOnClickListener {
            findNavController().popBackStack(R.id.analyzesFragment, false)
        }
        binding.tvRules.setOnClickListener{
            val format = "https://drive.google.com/viewerng/viewer?embedded=true&url=%s"
            val fullPath: String = java.lang.String.format(Locale.ENGLISH, format, "https://medic.madskill.ru/avatar/prav.pdf")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
            startActivity(browserIntent)
        }
    }
}