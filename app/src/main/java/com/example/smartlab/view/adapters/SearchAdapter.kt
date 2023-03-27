package com.example.smartlab.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartlab.databinding.SearchItemCardBinding
import com.example.smartlab.model.dto.CatalogItem

class SearchAdapter(
    private val context: Context,
    var items: List<CatalogItem>,
    var onItemClickListener: (CatalogItem) -> Unit = {}
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: SearchItemCardBinding) : RecyclerView.ViewHolder(binding.root)


    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(catalog: List<CatalogItem>) {
        this.items = catalog
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder( SearchItemCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTitle.text = item.name
            tvTimeResult.text = item.time_result
            tvPrice.text = "${item.price} ₽"
            root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}