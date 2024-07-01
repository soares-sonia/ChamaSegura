package com.example.queimasegura.common.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.queimasegura.R
import com.example.queimasegura.admin.AdminActivity
import com.example.queimasegura.common.register.RegisterActivity
import com.example.queimasegura.manager.ManagerActivity
import com.example.queimasegura.retrofit.model.send.LoginBody
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.user.UserActivity
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        initViewModels()

        initEvents()

        initObservers()
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = LoginViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
    }

    private fun initEvents() {
        val emailTextEdit = findViewById<EditText>(R.id.editTextEmail)
        val passwordTextEdit = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailTextEdit.text.toString()
            val password = passwordTextEdit.text.toString()

            if(inputCheck(email, password)){
                val myLoginBody = LoginBody(email, password)
                lifecycle.coroutineScope.launch {
                    viewModel.loginUser(myLoginBody)
                }
            }
        }

        findViewById<Button>(R.id.clickHereButton).setOnClickListener {
            navigateTo(RegisterActivity::class.java)
        }
    }

    private fun initObservers() {
        viewModel.auth.observe(this) { auth ->
            auth?.let {
                when(auth.type) {
                    0 -> navigateTo(UserActivity::class.java)
                    1 -> navigateTo(ManagerActivity::class.java)
                    2 -> navigateTo(AdminActivity::class.java)
                }
            }
        }
    }

    private fun inputCheck(email: String, password: String): Boolean {
        return !(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}

