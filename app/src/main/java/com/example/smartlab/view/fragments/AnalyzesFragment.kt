package com.example.smartlab.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartlab.databinding.CatalogChipBinding
import com.example.smartlab.databinding.FragmentAnalyzesBinding
import com.example.smartlab.view.adapters.CatalogAdapter
import com.example.smartlab.view.adapters.NewsAdapter
import com.example.smartlab.viewmodel.AnalyzesViewModel

class AnalyzesFragment : Fragment() {

    private val binding: FragmentAnalyzesBinding by lazy {
        FragmentAnalyzesBinding.inflate(layoutInflater)
    }
    private val viewModel: AnalyzesViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var catalogAdapter: CatalogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNews()
        viewModel.getCatalog()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.news.observe(viewLifecycleOwner) {
            with(binding.rvNews) {
                newsAdapter = NewsAdapter(requireContext(), it)
                adapter = newsAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }
        viewModel.catalog.observe(viewLifecycleOwner) {
            with(binding.rvCatalog) {
                catalogAdapter = CatalogAdapter(requireContext(), it)
                adapter = catalogAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (binding.chipGroup.isEmpty()) {
                categories.forEachIndexed { index, category ->
                    val chip =
                        CatalogChipBinding.inflate(layoutInflater).rootChip.apply {
                            text = category
                        }
                    binding.chipGroup.addView(chip.apply {
                        id = index
                    })
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }
}