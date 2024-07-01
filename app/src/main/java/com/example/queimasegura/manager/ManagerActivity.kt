package com.example.queimasegura.manager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.common.Pedido
import com.example.queimasegura.common.PedidoAdapter

class ManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manager_activity)

        val profileButton: ImageView = findViewById(R.id.profile_button)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
            Pedido("Queima", "2024-06-01", "Completed"),

        ).sortedByDescending { it.date }

        recyclerView.adapter = PedidoAdapter(this, pedidos)
    }
}
