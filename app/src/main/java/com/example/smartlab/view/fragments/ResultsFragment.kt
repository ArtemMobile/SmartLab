package com.example.smartlab.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentResultsBinding


class ResultsFragment : Fragment() {

    private val binding: FragmentResultsBinding by lazy{
        FragmentResultsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

}