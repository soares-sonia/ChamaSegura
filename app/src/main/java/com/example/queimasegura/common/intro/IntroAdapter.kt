package com.example.queimasegura.common.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R

class IntroAdapter : RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

    private val slideLayouts = listOf(
        R.layout.slide1,
        R.layout.slide2,
        R.layout.slide3
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return IntroViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        // Aqui você pode configurar o conteúdo dos slides se necessário
    }

    override fun getItemCount() = slideLayouts.size

    override fun getItemViewType(position: Int): Int {
        return slideLayouts[position]
    }

    inner class IntroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Configure as views dos slides se necessário
    }
}