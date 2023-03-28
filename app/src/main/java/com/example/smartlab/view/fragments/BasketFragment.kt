package com.example.smartlab.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentBasketBinding
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.view.adapters.BasketAdapter
import com.example.smartlab.viewmodel.BasketViewModel


class BasketFragment : Fragment() {

    private val binding: FragmentBasketBinding by lazy {
        FragmentBasketBinding.inflate(layoutInflater)
    }
    private val viewModel: BasketViewModel by viewModels()
    private lateinit var basketAdapter: BasketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
        initCartRecyclerView()
    }

    private fun setListeners() {
        binding.ivBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivClearAll.setOnClickListener {
            viewModel.clearAll()
        }
    }

    private fun setObservers() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            viewModel.cartItems = items.filter { it.isInCard }
            viewModel.cartItems.forEach {
                if (it.patientCount == 0) it.patientCount = 1
            }
            updateTotalPrice(viewModel.cartItems)
            basketAdapter.updateItems(viewModel.cartItems)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice(cartItems: List<CatalogItem>) {
        var totalPrice = 0
        cartItems.forEach {
            totalPrice += it.price.toInt() * it.patientCount
        }
        binding.tvTotalPrice.text = "$totalPrice ₽"
    }

    private fun initCartRecyclerView() {
        basketAdapter = BasketAdapter(
            requireContext(), listOf(),
            onMinusClickListener = { viewModel.onMinusClick(it) },
            onPlusClickListener = { viewModel.onPlusClick(it) },
            onDeleteClickListener = { viewModel.deleteFromCart(it) }
        )
        binding.rvCartItems.apply {
            adapter = basketAdapter
        }
    }


}