package com.example.smartlab.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.smartlab.R
import com.example.smartlab.databinding.CatalogItemCardBinding
import com.example.smartlab.model.dto.CatalogItem
import com.example.smartlab.utils.dpToPx

class CatalogAdapter(
    private val context: Context,
    var catalog: List<CatalogItem>,
    var onAddButtonClickListener: (CatalogItem) -> Unit = {},
    val onCardClickListener: (CatalogItem) -> Unit = {},
) : RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    class CatalogViewHolder(val binding: CatalogItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(CatalogItemCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalogItem = catalog[position]
        with(holder.binding) {
            tvName.text = catalogItem.name
            tvTimeResult.text = catalogItem.time_result
            tvPrice.text = "${catalogItem.price} ₽"
            with(btnAdd) {
                setOnClickListener {
                    onAddButtonClickListener(catalogItem)
                }
                if (catalogItem.isInCard) {
                    text = "Убрать"
                    setBackgroundColor(resources.getColor(R.color.white, null))
                    setTextColor(resources.getColor(R.color.blue_button, null))
                    setPadding(context.dpToPx(24.5), context.dpToPx(16.0), context.dpToPx(24.5), context.dpToPx(16.0))
                    strokeWidth = 1
                } else {
                    text = "Добавить"
                    setBackgroundColor(resources.getColor(R.color.blue_button, null))
                    setTextColor(resources.getColor(R.color.white, null))
                }
            }
            root.setOnClickListener {
                onCardClickListener(catalogItem)
            }
        }
        if (position == catalog.lastIndex) {
            holder.binding.catalogItemRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                setMargins(context.dpToPx(10.0), context.dpToPx(20.0), context.dpToPx(10.0), context.dpToPx(20.0))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(catalog: List<CatalogItem>) {
        this.catalog = catalog
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = catalog.size
}