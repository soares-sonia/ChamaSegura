package com.example.queimasegura.admin.fragments.home.fire

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.queimasegura.R
import com.example.queimasegura.admin.fragments.home.fire.model.UserIntent
import com.example.queimasegura.admin.fragments.home.fire.search.user.SearchUserActivity
import com.example.queimasegura.admin.fragments.home.fire.adapter.ReasonsAdapter
import com.example.queimasegura.admin.fragments.home.fire.map.MapActivity
import com.example.queimasegura.admin.fragments.home.fire.model.CreateFireDataIntent
import com.example.queimasegura.admin.fragments.home.fire.model.ZipcodeIntent
import com.example.queimasegura.admin.fragments.home.fire.search.zip.SearchActivity
import com.example.queimasegura.retrofit.model.send.CreateFireBody
import com.example.queimasegura.retrofit.repository.AdminRepository
import com.example.queimasegura.retrofit.repository.Repository
import com.example.queimasegura.room.entities.Reason
import com.example.queimasegura.room.entities.Type
import com.example.queimasegura.util.ApiUtils
import com.example.queimasegura.util.LocaleUtils
import com.example.queimasegura.util.NetworkUtils
import java.util.Calendar


class CreateFireActivity : AppCompatActivity() {
    private lateinit var viewModel: CreateFireViewModel
    private lateinit var spinnerReason: Spinner
    private lateinit var reasonsAdapter: ReasonsAdapter
    private lateinit var radioGroupType: RadioGroup

    private lateinit var datePicked: String
    private lateinit var zipcodeData: ZipcodeIntent
    private lateinit var userIntentData: UserIntent
    private lateinit var myDataIntent: CreateFireDataIntent
    private lateinit var lat: String
    private lateinit var lng: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_admin)

        initViewModels()

        initIntents()

        initVariables()

        initEvents()

        initObservers()

        handleInternetAccessibility()
    }

    private fun initViewModels() {
        val repository = AdminRepository()
        val viewModelFactory = CreateFireViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateFireViewModel::class.java]
    }

    private fun initIntents() {
        val zipcodeIntent = intent.getParcelableExtra<ZipcodeIntent>("selectedZipcode")
        zipcodeIntent?.let {
            zipcodeData = it
            handleLocationShow(it)
        }

        val userIntent = intent.getParcelableExtra<UserIntent>("selectedUser")
        userIntent?.let {
            userIntentData = it
            handleUserShow(it)
        }

        val intentData = intent.getParcelableExtra<CreateFireDataIntent>("parentData")
        intentData?.let {
            myDataIntent = it
            if(it.reasonId != null) {
                val spinner = findViewById<Spinner>(R.id.spinnerReason)
                spinner.setSelection(it.reasonId)
            }
            if(it.selectedDate != null) {
                datePicked = it.selectedDate
            }
            if(it.selectedDateString != null) {
                val textViewDate = findViewById<TextView>(R.id.textViewDateShow)
                textViewDate.text = it.selectedDateString
            }
        }

        lat = intent.getStringExtra("LAT") ?: ""
        lng = intent.getStringExtra("LNG") ?: ""
        if (lat.isNotEmpty() && lng.isNotEmpty()) {
            findViewById<TextView>(R.id.textViewOutputLocation).text = SpannableStringBuilder("$lat ; $lng")
        }
    }

    private fun initVariables() {
        spinnerReason = findViewById(R.id.spinnerReason)
        reasonsAdapter = ReasonsAdapter(this, mutableListOf())
        radioGroupType = findViewById(R.id.radioGroupType)

        viewModel.typesData.observe(this) { types ->
            types?.let{
                populateRadioGroup(it)
            }
        }

        viewModel.reasonsData.observe(this) { reasons ->
            reasons?.let {
                populateReasons(it)
            }
        }
    }

    private fun initEvents() {
        findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonPostCode).setOnClickListener {
            popUp(SearchActivity::class.java)
        }

        findViewById<Button>(R.id.buttonMap).setOnClickListener {
            popUp(MapActivity::class.java)
        }

        findViewById<Button>(R.id.buttonUser).setOnClickListener {
            popUp(SearchUserActivity::class.java)
        }

        findViewById<RadioGroup>(R.id.radioGroupType).setOnCheckedChangeListener{ group, checkedId ->
            for (i in 0 until group.childCount) {
                val radioButton = group.getChildAt(i) as RadioButton
                if (radioButton.id == checkedId) {
                    radioButton.setTextColor(getColor(R.color.black))
                } else {
                    radioButton.setTextColor(getColor(android.R.color.darker_gray))
                }
            }
        }

        findViewById<ImageButton>(R.id.imageButtonDate).setOnClickListener {
            showDatePickerDialog()
        }

        findViewById<Button>(R.id.buttonRegister).setOnClickListener{
            handleCreateFire()
        }
    }

    private fun initObservers() {
        viewModel.createFireResponse.observe(this) { response ->
            if(response.isSuccessful){
                showMessage(getString(R.string.create_fire_message))
                finish()
            }else if(response.errorBody() != null) {
                ApiUtils.handleApiError(this, response.errorBody(), ::showMessage)
            } else{
                showMessage(application.getString(R.string.server_error))
            }
        }
    }

    private fun handleInternetAccessibility() {
        if(!NetworkUtils.isInternetAvailable(application)) {
            val disabledBackground = ContextCompat.getDrawable(this, R.drawable.button_disabled_outline)
            if(!alreadyLoadTheMap()) {
                val mapButton = findViewById<Button>(R.id.buttonMap)
                mapButton.background = disabledBackground
                mapButton.setTextColor(getColor(android.R.color.darker_gray))
                mapButton.isEnabled = false
            }
        }
    }

    private fun handleCreateFire() {
        try{
            inputCheck()

            val selectedTypeId = findViewById<RadioButton>(radioGroupType.checkedRadioButtonId)?.id ?: throw IllegalArgumentException("Type not selected")
            val selectedReason = spinnerReason.selectedItem as Reason? ?: throw IllegalArgumentException("Reason not selected")

            val createFireBody= CreateFireBody(
                date = datePicked,
                typeId = selectedTypeId,
                reasonId = selectedReason.id,
                zipCodeId = zipcodeData.id,
                location = null,
                observations = null
            )
            viewModel.createFire(userIntentData.userId, createFireBody)
        } catch (error: Exception) {
            showMessage(error.message!!)
        }
    }

    private fun inputCheck() {
        if(!::datePicked.isInitialized)
            throw IllegalArgumentException(getString(R.string.create_fire_error_date))
        if(!::zipcodeData.isInitialized)
            throw IllegalArgumentException(getString(R.string.create_fire_error_location))
        if(!::userIntentData.isInitialized)
            throw IllegalArgumentException(getString(R.string.create_fire_error_user))
    }

    private fun handleLocationShow(location: ZipcodeIntent) {
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

        val locationOutput = findViewById<TextView>(R.id.textViewOutputLocation)
        locationOutput.text = locationStringBuilder
    }

    private fun handleUserShow(user: UserIntent) {
        val userStringBuilder = StringBuilder()

        userStringBuilder.append(user.nif)
        userStringBuilder.append(" - ")
        userStringBuilder.append(user.fullName)
        userStringBuilder.append(":")
        userStringBuilder.append(user.email)

        userStringBuilder.toString()

        val userOutput = findViewById<TextView>(R.id.textViewSelectedUser)
        userOutput.text = userStringBuilder
    }

    private fun populateRadioGroup(types: List<Type>) {
        val radioGroupType = findViewById<RadioGroup>(R.id.radioGroupType)
        radioGroupType.removeAllViews()
        var checkedId: Int
        if(::myDataIntent.isInitialized){
            checkedId = if(myDataIntent.typeId != null) myDataIntent.typeId!! else -1
        } else {
            checkedId = -1
        }

        val location = LocaleUtils.getUserPhoneLanguage(application)

        for (type in types) {
            val radioButton = RadioButton(this).apply {
                id = type.id
                text = if(location == "pt"){
                    type.namePt
                }else{
                    type.nameEn
                }
                textSize = 16f
                setTextColor(resources.getColor(R.color.black, null))
                buttonTintList = ContextCompat.getColorStateList(context, R.color.radio_btn_tint)
                typeface = ResourcesCompat.getFont(this@CreateFireActivity, R.font.karma_medium)
                layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    radioGroupType.check(id)
                }
            }
            radioGroupType.addView(radioButton)

            if (checkedId == -1 || checkedId == radioButton.id) {
                radioButton.isChecked = true
                checkedId = radioButton.id
            }
        }
    }

    private fun populateReasons(reasons: List<Reason>) {
        reasonsAdapter.clear()
        reasonsAdapter.addAll(reasons)
        spinnerReason.adapter = reasonsAdapter
        if(::myDataIntent.isInitialized){
            myDataIntent.reasonId?.let {
                spinnerReason.setSelection(it - 1)
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val language = LocaleUtils.getUserPhoneLanguage(this)
        val textViewDate = findViewById<TextView>(R.id.textViewDateShow)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            datePicked = "${selectedMonth + 1}/$selectedDay/$selectedYear"
            val formattedDate = if (language == "pt") {
                "${selectedDay}/${selectedMonth + 1}/$selectedYear"
            } else {
                "${selectedMonth + 1}/${selectedDay}/$selectedYear"
            }

            val dateString = getString(R.string.create_fire_date) + " " + formattedDate
            textViewDate.text = dateString
        },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun popUp(destination: Class<*>) {
        val textViewDate = findViewById<TextView>(R.id.textViewDateShow)
        val selectedType = findViewById<RadioButton>(radioGroupType.checkedRadioButtonId)

        val myDataIntent = CreateFireDataIntent(
            typeId = selectedType.id,
            reasonId = (spinnerReason.selectedItem as Reason?)?.id,
            selectedDate = if (::datePicked.isInitialized) datePicked else null,
            selectedDateString = textViewDate.text.toString()
        )

        val intent = Intent(this, destination)
        intent.putExtra("parentData", myDataIntent)
        if(::userIntentData.isInitialized) {
            intent.putExtra("selectedUser", userIntentData)
        }
        if(::zipcodeData.isInitialized) {
            intent.putExtra("selectedZipcode", zipcodeData)
        }
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_LONG).show()
    }

    private fun alreadyLoadTheMap(): Boolean {
        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val isCached = sharedPreferences.getBoolean("mapCached", false)
        return isCached
    }
}
