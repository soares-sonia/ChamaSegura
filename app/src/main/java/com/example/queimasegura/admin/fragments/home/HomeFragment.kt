package com.example.queimasegura.admin.fragments.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.home.fire.CreateFireActivity
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository


class HomeFragment : Fragment() {
    private  lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_a, container, false)

        initVariables(view)

        initEvents(view)

        return view
    }

    private fun initViewModels() {
        val repository = Repository()
        val adminRepository = AdminRepository()
        val viewModelFactory = HomeViewModelFactory(requireActivity().application, repository, adminRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun initVariables(view: View){
        val usernameTextView = view.findViewById<TextView>(R.id.username_welcome)
        viewModel.authData.observe(viewLifecycleOwner) { auth ->
            auth?.let {
                usernameTextView.text = it.fullName
            }
        }

        val pendingRequestsTextView = view.findViewById<TextView>(R.id.statusPending)
        val firePreventedTextView = view.findViewById<TextView>(R.id.statusCompleted)
        viewModel.statusData.observe(viewLifecycleOwner) { status ->
            status?.let {
                pendingRequestsTextView.text = it.firesPending.toString()
                firePreventedTextView.text = it.firesComplete.toString()
            }
        }
    }

    private fun initEvents(view: View) {
        val addImageView = view.findViewById<ImageView>(R.id.add)
        addImageView.setOnClickListener {
            startActivity(Intent(activity, CreateFireActivity::class.java))
        }
    }
}