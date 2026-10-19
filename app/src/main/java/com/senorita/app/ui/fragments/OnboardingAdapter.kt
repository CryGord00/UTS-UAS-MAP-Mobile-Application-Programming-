package com.senorita.app.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.senorita.app.R

class OnboardingAdapter(
    private val pages: List<OnboardingPageModel>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvOnboardingTitle)
        val desc: TextView = view.findViewById(R.id.tvOnboardingDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding_page, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = pages[position]
        holder.title.text = item.title
        holder.desc.text = item.description
    }

    override fun getItemCount(): Int = pages.size
}
