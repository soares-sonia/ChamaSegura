package com.example.queimasegura.common.detail.queima

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.retrofit.model.data.ZipCodeData
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.LocaleUtils


class QueimaDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel: QueimaDetailsViewModel

    private lateinit var authUSer: Auth
    private lateinit var fireId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queima_details)

        initViewModels()

        initIntents()

        initVariables()

        initObservers()

        initEvents()
    }

    private fun initViewModels() {
        val repository = Repository()
        val viewModelFactory = QueimaDetailsViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[QueimaDetailsViewModel::class.java]
    }

    private fun initIntents() {
        val status = intent.getStringExtra("STATUS") ?: ""
        findViewById<TextView>(R.id.status_text).text = status
        if(status != getString(R.string.fire_status_pending) && status != getString(R.string.fire_status_scheduled) && status != getString(R.string.fire_status_approved)) {
            val button = findViewById<Button>(R.id.buttonCancelRequest)
            button.visibility = View.GONE
        }

        fireId = intent.getStringExtra("ID") ?: ""
    }

    private fun initVariables() {
        val reasonTextView = findViewById<TextView>(R.id.reason_text)
        val dateTextView = findViewById<TextView>(R.id.date_text)
        val locationTextView = findViewById<TextView>(R.id.location_text)
        val obsTextView = findViewById<TextView>(R.id.obs_text)

        viewModel.responseDetails.observe(this) { response ->
            if(response.isSuccessful) {
                val language = LocaleUtils.getUserPhoneLanguage(this)
                response.body()?.result?.let {
                    reasonTextView.text = if(language == "pt") it.reason.namePt else it.reason.nameEn
                    dateTextView.text = it.fire.date
                    locationTextView.text = getLocationString(it.zipCode)
                    obsTextView.text = it.fire.observations
                }
            } else if(response.errorBody() != null) {
                ApiUtils.handleApiError(application, response.errorBody(), ::showMessage)
            } else {
                showMessage(getString(R.string.server_error))
            }
        }
    }

    private fun initObservers() {
        viewModel.authData.observe(this) { auth ->
            auth?.let {
                authUSer = it
                viewModel.fetchFireDetails(fireId, it)
            }
        }
        viewModel.cancelResponseDetails.observe(this) { response ->
            response?.let {
                if(it.isSuccessful) {
                    showMessage(getString(R.string.details_activity_message))
                    finish()
                }else if(response.errorBody() != null) {
                    ApiUtils.handleApiError(this, response.errorBody(), ::showMessage)
                } else {
                    showMessage(application.getString(R.string.server_error))
                }
            }
        }
    }

    private fun initEvents() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonCancelRequest).setOnClickListener {
            viewModel.cancelFire(fireId, authUSer)
        }
    }

    private fun getLocationString(location: ZipCodeData): String {
        val locationStringBuilder = StringBuilder()
        locationStringBuilder.append(location.zipCode)
        locationStringBuilder.append(", ")
        locationStringBuilder.append(location.locationName)

        location.artName?.let {
            if (it.isNotEmpty()) {
                locationStringBuilder.append(" - ")
                locationStringBuilder.append(it)
            }
        }

        location.tronco?.let {
            if (it.isNotEmpty()) {
                locationStringBuilder.append(" - ")
                locationStringBuilder.append(it)
            }
        }

        return locationStringBuilder.toString()
    }

    private fun showMessage(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }
}
