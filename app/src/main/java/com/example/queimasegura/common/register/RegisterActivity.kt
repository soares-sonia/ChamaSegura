package com.example.queimasegura.common.register

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.common.login.LoginActivity
import com.example.queimasegura.retrofit.model.ErrorApi
import com.example.queimasegura.retrofit.repository.Repository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        initViewModels()

        initIntent()

        initEvents()

        initObservers()
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = RegisterViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]
    }

    private fun initIntent() {
        val emailTextEdit = findViewById<EditText>(R.id.editTextEmail)
        val passTextEdit = findViewById<EditText>(R.id.editTextPass)
        val passConfirmTextEdit = findViewById<EditText>(R.id.editTextConfirmPass)

        val oldEmail = intent.getStringExtra("EMAIL")
        val oldPassword = intent.getStringExtra("PASSWORD")

        oldEmail?.let {
            emailTextEdit.setText(it)
        }
        oldPassword?.let {
            passTextEdit.setText(it)
            passConfirmTextEdit.setText(it)
        }
    }

    private fun initEvents() {
        val emailTextEdit = findViewById<EditText>(R.id.editTextEmail)
        val passTextEdit = findViewById<EditText>(R.id.editTextPass)
        val passConfirmTextEdit = findViewById<EditText>(R.id.editTextConfirmPass)

        findViewById<Button>(R.id.clickHereButton).setOnClickListener {
            navigateTo(LoginActivity::class.java)
        }

        findViewById<Button>(R.id.buttonContinue).setOnClickListener {
            val email = emailTextEdit.text.toString()
            val password = passTextEdit.text.toString()
            val confirm = passConfirmTextEdit.text.toString()

            try {
                inputCheck(email, password, confirm)
                viewModel.checkEmail(email)
            } catch (error: Exception) {
                showMessage(error.message!!)
            }
        }
    }

    private fun initObservers() {
        val emailTextEdit = findViewById<EditText>(R.id.editTextEmail)
        val passTextEdit = findViewById<EditText>(R.id.editTextPass)

        viewModel.checkEmailResponse.observe(this) { response ->
            if(response.isSuccessful) {
                val intent = Intent(this, CompleteInfoActivity::class.java).apply {
                    putExtra("EMAIL", emailTextEdit.text.toString())
                    putExtra("PASSWORD", passTextEdit.text.toString())
                }
                startActivity(intent)
            } else if(response.errorBody() != null) {
                handleError(response.errorBody()!!)
            } else{
                showMessage(application.getString(R.string.server_error))
            }
        }
    }

    private fun inputCheck(email: String, password: String, confirm: String) {
        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty())
            throw IllegalArgumentException("You must complete all fields")
        if (password.length < 6)
            throw IllegalArgumentException("Password must have more or 6 chars")
        if (!TextUtils.equals(password, confirm))
            throw IllegalArgumentException("Password and Confirm Password must be equal")
    }

    private fun handleError(errorBody: ResponseBody) {
        val gson = Gson()
        val type = object : TypeToken<ErrorApi>() {}.type
        val errorApiResponse: ErrorApi? = gson.fromJson(errorBody.charStream(), type)
        if(errorApiResponse != null) {
            showMessage(errorApiResponse.detail)
        } else{
            showMessage(application.getString(R.string.server_error))
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}
