package com.example.smartlab.view.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smartlab.model.dto.OnboardingItem
import com.example.smartlab.view.fragments.OnboardingItemFragment

class OnboardingAdapter(
    val fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    var items: MutableList<OnboardingItem> = mutableListOf()
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return OnboardingItemFragment.newInstance(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePages(items: MutableList<OnboardingItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}