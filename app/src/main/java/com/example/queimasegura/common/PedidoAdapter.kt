package com.example.queimasegura.common

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.common.detail.queima.QueimaDetailsActivity
import com.example.queimasegura.common.detail.queimada.QueimadaDetailsActivity


class PedidoAdapter(
    private val context: Context,
    private val pedidos: List<Pedido>
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_card, parent, false)
        return PedidoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.typeTextView.text = pedido.type
        holder.dateTextView.text = pedido.date
        holder.stateTextView.text = pedido.state

        holder.itemView.setOnClickListener {
            val intent = when (pedido.type) {
                "Queima" -> Intent(context, QueimaDetailsActivity::class.java)
                "Queimada" -> Intent(context, QueimadaDetailsActivity::class.java)
                else -> return@setOnClickListener
            }
            intent.apply {
                putExtra("type", pedido.type)
                putExtra("date", pedido.date)
                putExtra("state", pedido.state)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = pedidos.size

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.type)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val stateTextView: TextView = itemView.findViewById(R.id.state)
    }
}
