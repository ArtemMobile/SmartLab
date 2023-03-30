package com.example.smartlab.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartlab.R
import com.example.smartlab.databinding.BasketCardBinding
import com.example.smartlab.model.dto.CatalogItem

class BasketAdapter(
    private val context: Context,
    var items: List<CatalogItem>,
    val onMinusClickListener: (CatalogItem) -> Unit = {},
    val onPlusClickListener: (CatalogItem) -> Unit = {},
    val onDeleteClickListener: (CatalogItem) -> Unit = {},
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    class BasketViewHolder(val binding: BasketCardBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<CatalogItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder =
        BasketViewHolder(BasketCardBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTitle.text = item.name
            tvPrice.text = "${item.price} ₽"
            tvPatientCount.text = showPatientCount(item.patientCount)
            ivDelete.setOnClickListener {
                onDeleteClickListener(item)
            }
            if (item.patientCount == 1) {
                ivMinus.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.minus_inactive))
            } else {
                ivMinus.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.minus_active))
            }
            ivMinus.setOnClickListener {
                if (item.patientCount > 1) {
                    onMinusClickListener(item)
                }
            }
            ivPlus.setOnClickListener {
                onPlusClickListener(item)
            }
        }
    }

    private fun showPatientCount(count: Int): String {
        val lastTwoDigits = (count % 1000) % 100
        return if((lastTwoDigits % 10 == 1 && lastTwoDigits > 20) || lastTwoDigits == 1)
            "$lastTwoDigits пациент"
        else if((lastTwoDigits % 10 in 2..4) && lastTwoDigits / 10 != 1)
            "$lastTwoDigits пациента"
        else
            "$lastTwoDigits пациентов"
    }

    override fun getItemCount(): Int = items.size
}