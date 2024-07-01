package com.example.queimasegura.admin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.admin.model.Rule

class RuleAdapter(private val rulesList: MutableList<Rule>, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<RuleAdapter.RuleViewHolder>() {

    class RuleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val district: TextView = view.findViewById(R.id.district)
        val startDate: TextView = view.findViewById(R.id.startDate)
        val endDate: TextView = view.findViewById(R.id.endDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rule_row_item, parent, false)
        return RuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val rule = rulesList[position]
        holder.district.text = rule.district
        holder.startDate.text = rule.starDate
        holder.endDate.text = rule.endDate

        val backgroundColor = if (position % 2 == 0) {
            holder.itemView.context.getColor(R.color.white)
        } else {
            holder.itemView.context.getColor(R.color.colorAccent)
        }
        holder.itemView.setBackgroundColor(backgroundColor)

        holder.itemView.setOnClickListener {
            itemClickListener(position)
        }
    }

    override fun getItemCount() = rulesList.size

    fun deleteRule(position: Int) {
        rulesList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, rulesList.size)
    }
}