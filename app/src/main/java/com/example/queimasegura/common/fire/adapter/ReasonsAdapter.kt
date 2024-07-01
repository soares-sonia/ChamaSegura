package com.example.queimasegura.common.fire.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.queimasegura.R
import com.example.queimasegura.room.entities.Reason
import com.example.queimasegura.util.LocaleUtils


class ReasonsAdapter(context: Context, reasons: List<Reason>) :
    ArrayAdapter<Reason>(context, 0, reasons) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val reason = getItem(position) ?: throw IllegalStateException("Reason at position $position not found")

        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))

        val textView = view.findViewById<TextView>(android.R.id.text1)

        val location = LocaleUtils.getUserPhoneLanguage(context)
        textView.text = if(location == "pt"){
            reason.namePt
        }else{
            reason.nameEn
        }

        textView.setTextColor(ContextCompat.getColor(context, R.color.black))

        return view
    }
}