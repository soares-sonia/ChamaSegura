package com.example.queimasegura.common.fire.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.example.queimasegura.R

class QueimadaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_queimada, container, false)

        view.findViewById<ImageButton>(R.id.imageButtonDropDownFireTech).setOnClickListener {
            handleDropDownMenu(it, R.menu.temp_dropdown_fire_tech)
        }

        return  view
    }

    private fun handleDropDownMenu(view: View, menuId: Int) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            showToast(item.title.toString())
            true
        }

        popupMenu.show()
    }

    private fun showToast(str: String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
    }

}