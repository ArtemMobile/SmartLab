package com.example.smartlab.view.adapters

import android.content.Context
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
            btnAdd.setOnClickListener {
                onAddButtonClickListener(catalogItem)
            }
        }
        if (position == catalog.lastIndex) {
            holder.binding.catalogItemRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                setMargins(context.dpToPx(20), context.dpToPx(20), context.dpToPx(20), context.dpToPx(20))
            }
        }
    }

    override fun getItemCount(): Int = catalog.size
}