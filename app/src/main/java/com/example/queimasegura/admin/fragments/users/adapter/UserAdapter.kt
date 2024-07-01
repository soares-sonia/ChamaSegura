package com.example.queimasegura.admin.fragments.users.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.users.model.User


class UserAdapter(
    private val context: Context,
    private val userList: MutableList<User>,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username)
        val email: TextView = view.findViewById(R.id.email)
        var type: TextView = view.findViewById(R.id.type)
    }

    private var filterUserList: MutableList<User> = userList.toMutableList()
    private var currentFilterStatus: String = ""
    private var searchQuery: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filterUserList[position]
        holder.username.text = user.fullName
        holder.email.text = user.email
        holder.type.text = when (user.type) {
            2 -> context.getString(R.string.admin)
            1 -> context.getString(R.string.manager)
            else -> context.getString(R.string.user)
        }

        Log.d("USER", user.toString())
        if (user.deleted) {
            holder.username.setTextColor(context.getColor(android.R.color.holo_red_dark))
        } else if (!user.active) {
            holder.username.setTextColor(context.getColor(android.R.color.holo_orange_dark))
        } else {
            holder.username.setTextColor(context.getColor(android.R.color.black))
        }

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

    override fun getItemCount() = filterUserList.size

    fun updateUserPermission(position: Int, newPermission: Int): Int {
        val user = filterUserList[position]
        userList.find { it.id == user.id }?.type = newPermission
        user.type = newPermission
        applyCurrentFilterAndSearch()
        return user.type
    }

    fun banUser(position: Int): Boolean {
        val user = filterUserList[position]
        val originalUser = userList.find { it.id == user.id }
        user.active = !user.active
        originalUser?.active = user.active
        applyCurrentFilterAndSearch()
        return user.active
    }

    fun deleteUser(position: Int): Boolean {
        val user = filterUserList[position]
        val originalUser = userList.find { it.id == user.id }
        user.deleted = !user.deleted
        originalUser?.deleted = user.deleted
        applyCurrentFilterAndSearch()
        return user.deleted
    }

    fun filterByStatus(filterStatus: String) {
        currentFilterStatus = filterStatus
        applyCurrentFilterAndSearch()
    }

    fun filterBySearchQuery(query: String) {
        searchQuery = query
        applyCurrentFilterAndSearch()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyCurrentFilterAndSearch() {
        filterUserList = userList.filter { user ->
            val matchesFilter = when (currentFilterStatus) {
                context.getString(R.string.active_filter) -> user.active && !user.deleted
                context.getString(R.string.banned_filter) -> !user.active && !user.deleted
                context.getString(R.string.del_filter) -> user.deleted
                else -> true
            }
            val matchesSearch = user.fullName.contains(searchQuery, true) || user.email.contains(searchQuery, true)
            matchesFilter && matchesSearch
        }.toMutableList()
        notifyDataSetChanged()
    }
}

