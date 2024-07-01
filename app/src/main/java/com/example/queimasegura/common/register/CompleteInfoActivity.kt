package com.example.queimasegura.common.register

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.common.login.LoginActivity
import com.example.queimasegura.retrofit.model.send.CreateUserBody
import com.example.queimasegura.retrofit.model.ErrorApi
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.user.UserActivity
import com.example.queimasegura.util.ApiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

class CompleteInfoActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_info)

        initViewModels()

        initIntents()

        initEvents()

        initObservers()
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = RegisterViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]
    }

    private fun initIntents() {
        email = intent.getStringExtra("EMAIL") ?: ""
        password = intent.getStringExtra("PASSWORD") ?: ""
    }

    private fun initEvents() {
        val fullNameTextEdit = findViewById<EditText>(R.id.editTextName)
        val nifTextEdit = findViewById<EditText>(R.id.editTextNif)

        findViewById<Button>(R.id.buttonFinish).setOnClickListener {
            val fullName = fullNameTextEdit.text.toString()
            val nif = nifTextEdit.text.toString()

            try {
                inputCheck(fullName, nif)

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.createUser(CreateUserBody(fullName, email, password, nif))
                } else {
                    showMessage("Email or Password is missing")
                }
            } catch (error: Exception) {
                showMessage(error.message!!)
            }
        }

        findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                putExtra("EMAIL", email)
                putExtra("PASSWORD", password)
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.clickHereButton).setOnClickListener {
            navigateTo(LoginActivity::class.java)
        }

        findViewById<ImageButton>(R.id.imageButtonAdd).setOnClickListener {
            showMessage("Add logic")
        }
    }

    private fun initObservers() {
        viewModel.createUserResponse.observe(this) { response ->
            if (response.isSuccessful) {
                showMessage(application.getString(R.string.register_message_success))
                navigateTo(UserActivity::class.java)
            } else if(response.errorBody() != null) {
                ApiUtils.handleApiError(this, response.errorBody(), ::showMessage)
            } else{
                showMessage(application.getString(R.string.server_error))
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }

    private fun inputCheck(name: String, nif: String) {
        if (TextUtils.isEmpty(name))
            throw IllegalArgumentException("Name cannot be empty")
        if (TextUtils.isEmpty(nif))
            throw IllegalArgumentException("NIF cannot be empty")
        if (nif.length != 9 || !nif.all { it.isDigit() })
            throw IllegalArgumentException("NIF must be a 9-digit number")
    }
}
