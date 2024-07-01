package com.example.queimasegura.admin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queimasegura.R
import com.example.queimasegura.admin.adapters.RuleAdapter
import com.example.queimasegura.admin.model.Rule
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RulesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RuleAdapter
    private lateinit var rulesList: MutableList<Rule>
    private var isDeleteMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rules, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewRulesTable)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val sampleDate = getTodayDate();

        rulesList = MutableList(15) { index ->
            Rule("District$index", sampleDate, sampleDate)
        }

        adapter = RuleAdapter(rulesList) { position ->
            if (isDeleteMode) {
                adapter.deleteRule(position)
                isDeleteMode = false
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        val buttonAdd: Button = view.findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            showAddRuleFragment()
        }

        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            isDeleteMode = true
            Toast.makeText(requireContext(), "Select an item to delete", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = Date()

        return dateFormat.format(today)
    }

    private fun showAddRuleFragment() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_rule, null)
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Add Rule")

        val dialog = dialogBuilder.create()
        dialog.show()

        dialogView.findViewById<ImageButton>(R.id.imageButtonDropDownDistrict).setOnClickListener {
            handleDropDownMenu(it, R.menu.temp_dropdown_district, dialogView.findViewById(R.id.textViewDistrict))
        }

        dialogView.findViewById<ImageButton>(R.id.imageButtonStartDate).setOnClickListener {
            showDatePickerDialog(dialogView.findViewById(R.id.textViewStartDate))
        }

        dialogView.findViewById<ImageButton>(R.id.imageButtonEndDate).setOnClickListener {
            showDatePickerDialog(dialogView.findViewById(R.id.textViewEndDate))
        }

        dialogView.findViewById<Button>(R.id.buttonConfirm).setOnClickListener {
            val district: String = dialogView.findViewById<TextView>(R.id.textViewDistrict).text.toString()
            val startDate: String = dialogView.findViewById<TextView>(R.id.textViewStartDate).text.toString()
            val endDate: String = dialogView.findViewById<TextView>(R.id.textViewEndDate).text.toString()

            val isValid = handleConfirmation(district, startDate, endDate)

            if (isValid) {
                val newRule = Rule(district, startDate, endDate)
                rulesList.add(newRule)
                adapter.notifyItemInserted(rulesList.size -1)
                dialog.dismiss()
            }
        }
    }

    private fun handleDropDownMenu(view: View, menuId: Int, textView: TextView) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(menuId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            textView.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            textView.text = selectedDate
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun handleConfirmation(district: String, startDate: String, endDate: String): Boolean {
        if (district.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            return false
        }

        val startDateParsed = parseDate(startDate)
        val endDateParsed = parseDate(endDate)

        if (startDateParsed == null || endDateParsed == null || startDateParsed.after(endDateParsed)) {
            Toast.makeText(requireContext(), "End date must be after start date!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun parseDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }
}