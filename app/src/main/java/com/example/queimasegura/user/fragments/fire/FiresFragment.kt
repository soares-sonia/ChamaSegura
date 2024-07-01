package com.example.queimasegura.user.fragments.fire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.user.fragments.fire.adapter.FireAdapter


class FiresFragment : Fragment() {
    private lateinit var viewModel: FiresViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var fireAdapter: FireAdapter
    private lateinit var fires: List<Fire>
    private lateinit var textViewStateHeaderTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModels()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requests_u, container, false)

        initVariables(view)

        initEvents(view)

        return view
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = FiresViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[FiresViewModel::class.java]
    }

    private fun initVariables(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        textViewStateHeaderTextView = view.findViewById(R.id.textViewStateHeader)

        viewModel.firesData.observe(viewLifecycleOwner) { firesData ->
            firesData?.let {
                fires = it
                fireAdapter = FireAdapter(requireContext(), it)
                recyclerView.adapter = fireAdapter
            }
        }
    }

    private fun initEvents(view: View) {
        view.findViewById<ImageButton>(R.id.imageButtonDate).setOnClickListener {
            showFilterOptions(it)
        }
    }

    private fun showFilterOptions(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.blank -> {
                    textViewStateHeaderTextView.text = getString(R.string.state)
                    fireAdapter.updateFires(fires)
                }
                R.id.pending -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_pending)
                    filterFiresByState(getString(R.string.fire_status_pending))
                }
                R.id.scheduled -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_scheduled)
                    filterFiresByState(getString(R.string.fire_status_scheduled))
                }
                R.id.ongoing -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_ongoing)
                    filterFiresByState(getString(R.string.fire_status_ongoing))
                }
                R.id.completed -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_completed)
                    filterFiresByState(getString(R.string.fire_status_completed))
                }
                R.id.approved -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_approved)
                    filterFiresByState(getString(R.string.fire_status_approved))
                }
                R.id.refuse -> {
                    textViewStateHeaderTextView.text = getString(R.string.fire_status_refuse)
                    filterFiresByState(getString(R.string.fire_status_refuse))
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun filterFiresByState(state: String) {
        val filteredFires = fires.filter { it.status == state }
        fireAdapter.updateFires(filteredFires)
    }
}
