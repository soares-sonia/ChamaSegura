package com.example.queimasegura.common.fire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.queimasegura.R

class SearchListAdapter(context: Context, private val items: List<String>): ArrayAdapter<String>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.search_row_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.textItem)
        textView.text = items[position]

        textView.setTextColor(context.getColor(android.R.color.darker_gray))
        return view
    }
}