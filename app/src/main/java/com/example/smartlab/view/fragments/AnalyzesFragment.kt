package com.example.smartlab.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartlab.databinding.CatalogChipBinding
import com.example.smartlab.databinding.FragmentAnalyzesBinding
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.model.dto.NewsItem
import com.example.smartlab.view.adapters.CatalogAdapter
import com.example.smartlab.view.adapters.NewsAdapter
import com.example.smartlab.viewmodel.AnalyzesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnalyzesFragment : Fragment() {

    private val binding: FragmentAnalyzesBinding by lazy {
        FragmentAnalyzesBinding.inflate(layoutInflater)
    }
    private val viewModel: AnalyzesViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var catalogAdapter: CatalogAdapter
    private lateinit var categoriesList: List<String>
    private lateinit var catalogList: List<CatalogItem>
    private lateinit var newsList: List<NewsItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNews()
        viewModel.getCatalog()
        setUpObservers()
        applyChips()
        initSwipeRefreshLayout()
        setListeners()
    }

    private fun setUpObservers() {
        viewModel.news.observe(viewLifecycleOwner) {
            with(binding.rvNews) {
                newsList = it
                initNewsRecycler()
            }
        }
        viewModel.catalog.observe(viewLifecycleOwner) {
            with(binding.rvCatalog) {
                catalogList = it
                initCatalogRecycler()
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (binding.chipGroup.isEmpty()) {
                categoriesList = categories
                categories.forEachIndexed { index, category ->
                    val chip = CatalogChipBinding.inflate(layoutInflater).rootChip.apply {
                        text = category
                    }
                    binding.chipGroup.addView(chip.apply { id = index })
                }
            }
        }
    }

    private fun setListeners() {
        binding.etSearch.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                binding.tvCancel.visibility = View.VISIBLE
                binding.mainContainer.visibility = View.GONE
            } else {
                binding.tvCancel.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
            }
        }
        binding.tvCancel.setOnClickListener {
            binding.etSearch.clearFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    private fun applyChips() {
        with(binding.chipGroup) {
            setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    filterList(checkedIds[0])
                } else {
                    initCatalogRecycler()
                }
            }
        }
    }

    private fun filterList(id: Int) {
        with(binding.rvCatalog) {
            catalogAdapter = CatalogAdapter(requireContext(),
                catalogList.filter { it.category == categoriesList.toList()[id] })
            adapter = catalogAdapter
        }
    }

    private fun initCatalogRecycler() {
        with(binding.rvCatalog) {
            catalogAdapter = CatalogAdapter(requireContext(), catalogList)
            adapter = catalogAdapter
        }
    }

    private fun initNewsRecycler() {
        with(binding.rvNews) {
            newsAdapter = NewsAdapter(requireContext(), newsList)
            adapter = newsAdapter
        }
    }

    private fun initSwipeRefreshLayout() {
        binding.root.setOnRefreshListener {
            lifecycleScope.launch {
                delay(1000)
                viewModel.getNews()
                viewModel.getCatalog()
                binding.root.isRefreshing = false
            }
        }
    }
}