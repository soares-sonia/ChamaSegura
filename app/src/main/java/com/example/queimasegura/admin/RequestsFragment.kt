package com.example.queimasegura.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.common.Pedido
import com.example.queimasegura.common.PedidoAdapter

class RequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requests_a, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val pedidos = listOf(
            Pedido("Queima", "2024-06-11", "Pending"),
            Pedido("Queimada", "2024-06-10", "Completed"),
            Pedido("Queima", "2024-06-09", "In Progress"),
            Pedido("Queima", "2024-06-08", "Pending"),
            Pedido("Queima", "2024-06-07", "Completed"),
            Pedido("Queimada", "2024-06-06", "In Progress"),
            Pedido("Queima", "2024-06-05", "Pending"),
            Pedido("Queima", "2024-06-04", "Completed"),
            Pedido("Queimada", "2024-06-03", "In Progress"),
            Pedido("Queima", "2024-06-02", "Pending"),
            Pedido("Queima", "2024-06-01", "Completed"),)
            .sortedByDescending { it.date }

        recyclerView.adapter = PedidoAdapter(requireContext(), pedidos)

        return view
    }
}
