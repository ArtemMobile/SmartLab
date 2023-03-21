package com.example.smartlab.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentAnalyzesBinding
import com.google.android.material.chip.Chip


class AnalyzesFragment : Fragment() {

    private val binding: FragmentAnalyzesBinding by lazy {
        FragmentAnalyzesBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFilterList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpFilterList() {
        with(binding) {
            val list =
                mutableListOf("популярное", "COVID", "популярное", "популярное", "популярное")
            for (type in list) {
                val chip = Chip(requireContext()).apply {
                    text = type

                    textSize = 25.5F
                    setTextColor(resources.getColor(R.color.white, null))
                    backgroundDrawable = resources.getDrawable(R.drawable.catalog_selector, null)
                }
                categoriesGroup.addView(chip)
            }
        }
    }
}
