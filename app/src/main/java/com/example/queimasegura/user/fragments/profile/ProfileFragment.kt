package com.example.queimasegura.user.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.repository.Repository


class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_u, container, false)

        initVariables(view)

        initEvents(view)

        return view
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = ProfileViewModelFactory(requireActivity().application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
    }

    private fun initVariables(view: View) {
        val userNameTextView = view.findViewById<TextView>(R.id.profile_userName)
        val emailTextView = view.findViewById<TextView>(R.id.profile_email)
        val fullNameTextView = view.findViewById<TextView>(R.id.profile_fullName)
        val nifTextView = view.findViewById<TextView>(R.id.profile_nif)
        val phoneTextView = view.findViewById<TextView>(R.id.profile_phone)

        viewModel.authData.observe(viewLifecycleOwner) { auth ->
            auth?.let {
                userNameTextView.text = it.fullName
                emailTextView.text = it.email
                fullNameTextView.text = it.fullName
                nifTextView.text = it.nif.toString()
                phoneTextView.text = it.nif.toString()
            }
        }
    }

    private fun initEvents(view: View) {
        view.findViewById<Button>(R.id.profile_logout).setOnClickListener {
            viewModel.logoutUser()
        }
    }

}
