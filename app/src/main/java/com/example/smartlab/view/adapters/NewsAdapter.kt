package com.example.smartlab.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartlab.databinding.NewsItemCardBinding
import com.example.smartlab.model.dto.NewsItem

class NewsAdapter(private val context: Context, var news: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: NewsItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(NewsItemCardBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = news[position]
        with(holder.binding) {
            tvName.text = newsItem.name
            tvDescription.text = newsItem.description
            tvPrice.text = "${newsItem.price} ₽"
            Glide.with(context)
                .load(newsItem.image)
                .into(ivNews)
        }
        if (position == news.lastIndex) {
            holder.binding.newsRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                setMargins(0, 0, 0, 0)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(news: List<NewsItem>){
        this.news = news
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = news.size
}
