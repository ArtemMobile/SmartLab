package com.example.smartlab.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartlab.R
import com.example.smartlab.databinding.BottomSheetAnalyzeBinding
import com.example.smartlab.databinding.CatalogChipBinding
import com.example.smartlab.databinding.FragmentAnalyzesBinding
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.view.adapters.CatalogAdapter
import com.example.smartlab.view.adapters.NewsAdapter
import com.example.smartlab.view.adapters.SearchAdapter
import com.example.smartlab.viewmodel.AnalyzesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnalyzesFragment : Fragment() {
    private val binding: FragmentAnalyzesBinding by lazy {
        FragmentAnalyzesBinding.inflate(layoutInflater)
    }
    private val viewModel: AnalyzesViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var catalogAdapter: CatalogAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var categoriesList: List<String>
    private lateinit var catalogList: List<CatalogItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initSwipeRefreshLayout()
        initNewsRecyclerView()
        initCatalogRecyclerView()
        initSearchRecyclerView()
        viewModel.getNews()
        viewModel.getCatalog()
        setObservers()
        //applyChips()
    }

    override fun onResume() {
        super.onResume()
        binding.searchResultsContainer.visibility = View.GONE
    }

    private fun setListeners() {
        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tvCancel.visibility = View.VISIBLE
                binding.mainContainer.visibility = View.INVISIBLE
                binding.searchResultsContainer.visibility = View.VISIBLE
                binding.etSearch.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        viewModel.catalog.value?.let {
                            binding.searchResultsContainer.visibility = View.VISIBLE
                            val searchItems = it.filter { catalogItem ->
                                catalogItem.name.lowercase().contains(s.toString().lowercase())
                            }
                            searchAdapter.updateItems(searchItems)
                        }
                    }
                })
            } else {
                binding.tvCancel.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
                binding.searchResultsContainer.visibility = View.GONE
            }
        }
        binding.tvCancel.setOnClickListener {
            binding.etSearch.apply {
                clearFocus()
                setText("")
                binding.searchResultsContainer.visibility = View.GONE
            }
        }
        binding.btnGoToBasket.setOnClickListener {
            findNavController().navigate(R.id.action_analyzesFragment_to_basketFragment)
        }
    }

    private fun applyChips() {
        with(binding.chipGroup) {
            setOnCheckedStateChangeListener { _, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    filterList(checkedIds[0])
                } else {
                    catalogAdapter.updateItems(catalogList)
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

    private fun setObservers() {
        viewModel.news.observe(viewLifecycleOwner) {
            newsAdapter.updateItems(it)
        }
        viewModel.catalog.observe(viewLifecycleOwner) {
            viewModel.dbCatalog.value?.let { catalogDb ->
                if (catalogDb.isEmpty()) {
                    viewModel.fillDatabase(it)
                    catalogAdapter.updateItems(it)
                }
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (binding.chipGroup.isEmpty()) {
                categoriesList = categories
                categories.forEachIndexed { index, category ->
                    val chip = CatalogChipBinding.inflate(layoutInflater).rootChip.apply {
                        text = category
                    }
                    binding.chipGroup.addView(chip.apply {
                        id = index
                    })
                }
            }
        }
        viewModel.dbCatalog.observe(viewLifecycleOwner) { dbCatalog ->
            catalogList = dbCatalog
            var cartItemPrice = 0
            val cartItems = dbCatalog.filter { item -> item.isInCard }
            cartItems.forEach {
                cartItemPrice += it.price.trimEnd(' ', '₽').toInt()
            }
            viewModel.cartTotalPrice.value = cartItemPrice
            catalogAdapter.updateItems(dbCatalog)
        }
        viewModel.cartTotalPrice.observe(viewLifecycleOwner) {
            binding.goToBasketContainer.visibility = View.INVISIBLE
            if (it > 0) {
                binding.goToBasketContainer.visibility = View.VISIBLE
                binding.tvCartPrice.text = "$it ₽"
            }
        }
    }

    private fun initNewsRecyclerView() {
        newsAdapter = NewsAdapter(requireContext(), listOf())
        binding.rvNews.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }
    }

    private fun showAnalyzeDialog(analyzeItem: CatalogItem) {
        val analyzeBinding = BottomSheetAnalyzeBinding.inflate(layoutInflater)
        val analyzeDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheet)
        analyzeBinding.apply {
            tvTitle.text = analyzeItem.name
            tvDescription.text = analyzeItem.description
            tvPreparation.text = analyzeItem.preparation
            tvTimerResult.text = analyzeItem.time_result
            tvBio.text = analyzeItem.bio
            btnAdd.apply {
                text = "Добавить за ${analyzeItem.price} ₽"
                setOnClickListener{
                    viewModel.updateCatalogItem(analyzeItem.copy(isInCard = !analyzeItem.isInCard))
                    analyzeDialog.cancel()
                }
            }
            ivClose.setOnClickListener { analyzeDialog.cancel() }
        }
        analyzeDialog.setContentView(analyzeBinding.root)
        analyzeDialog.show()
    }

    private fun initSearchRecyclerView() {
        searchAdapter = SearchAdapter(requireContext(), listOf())
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun initCatalogRecyclerView() {
        catalogAdapter = CatalogAdapter(
            requireContext(), listOf(),
            onCardClickListener = {
                showAnalyzeDialog(it)
            },
            onAddButtonClickListener = {
                viewModel.updateCatalogItem(it.copy(isInCard = !it.isInCard))
            }
        )
        binding.rvCatalog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = catalogAdapter
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