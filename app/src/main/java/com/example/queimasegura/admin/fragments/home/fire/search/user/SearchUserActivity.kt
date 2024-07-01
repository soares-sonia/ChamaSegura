package com.example.queimasegura.admin.fragments.home.fire.search.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.home.fire.model.UserIntent
import com.example.queimasegura.admin.fragments.home.fire.CreateFireActivity
import com.example.queimasegura.admin.fragments.home.fire.adapter.SearchListAdapter
import com.example.queimasegura.admin.fragments.home.fire.model.CreateFireDataIntent
import com.example.queimasegura.admin.fragments.home.fire.model.ZipcodeIntent
import com.example.queimasegura.retrofit.model.admin.data.AdminSearchUser
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.util.ApiUtils



class SearchUserActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchUserViewModel
    private lateinit var adapter: ArrayAdapter<String>

    private var users: List<AdminSearchUser> = listOf()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val searchDelay: Long = 500

    private lateinit var parentData: CreateFireDataIntent
    private lateinit var zipcodeData: ZipcodeIntent

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViewModels()

        initIntents()

        initEvents()

        initObservers()
    }

    private fun initViewModels() {
        val adminRepository = AdminRepository()
        val viewModelFactory = SearchUserViewModelFactory(application, adminRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchUserViewModel::class.java]
    }

    private fun initIntents() {
        val parentDataIntent = intent.getParcelableExtra<CreateFireDataIntent>("parentData")
        parentDataIntent?.let {
            parentData = it
        }

        val zipcodeIntent = intent.getParcelableExtra<ZipcodeIntent>("selectedZipcode")
        zipcodeIntent?.let {
            zipcodeData = it
        }
    }

    private fun initEvents() {
        val searchView = findViewById<SearchView>(R.id.searchView)

        setSearchViewTextColor(searchView, R.color.black)

        val listView = findViewById<ListView>(R.id.suggestions_list)

        adapter = SearchListAdapter(this, mutableListOf())
        listView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if(query.isNotEmpty()) {
                        viewModel.getUsers(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable {
                    newText?.let {
                        if (it.isNotEmpty()) {
                            viewModel.getUsers(it)
                        }
                    }
                }
                handler.postDelayed(searchRunnable!!, searchDelay)
                return true
            }
        })

        findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            goBack()
        }

        findViewById<ListView>(R.id.suggestions_list).setOnItemClickListener { _, _, position, _ ->
            val user = users[position]
            val userIntent = UserIntent(
                userId = user.userId,
                fullName = user.fullName,
                email = user.email,
                nif = user.nif,
            )

            goBack(userIntent)
        }
    }

    private fun initObservers() {
        viewModel.locationResponse.observe(this) { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    users = it.result
                    updateListView(it.result)
                }
            } else if(response.errorBody() != null) {
                ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
            } else{
                showMessage(application.getString(R.string.server_error))
            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun setSearchViewTextColor(searchView: SearchView, color: Int) {
        val searchTextField = SearchView::class.java.getDeclaredField("mSearchSrcTextView")
        searchTextField.isAccessible = true
        val searchText = searchTextField.get(searchView) as EditText
        searchText.setTextColor(getColor(color))
    }


    private fun updateListView(users: List<AdminSearchUser>) {
        val usersStrings = users.map { user ->
            val userStringBuilder = StringBuilder()

            userStringBuilder.append(user.nif)
            userStringBuilder.append(" - ")
            userStringBuilder.append(user.fullName)
            userStringBuilder.append(":")
            userStringBuilder.append(user.email)

            userStringBuilder.toString()
        }

        adapter.clear()
        adapter.addAll(usersStrings)
        adapter.notifyDataSetChanged()
    }

    private fun goBack(userIntent: UserIntent? = null) {
        val intent = Intent(this, CreateFireActivity::class.java)
        if(userIntent != null){
            intent.putExtra("selectedUser", userIntent)
        }
        intent.putExtra("parentData", parentData)
        if(::zipcodeData.isInitialized) {
            intent.putExtra("selectedZipcode", zipcodeData)
        }
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
