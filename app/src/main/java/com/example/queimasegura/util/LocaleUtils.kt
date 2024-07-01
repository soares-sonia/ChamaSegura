package com.example.queimasegura.util

import android.content.Context
import java.util.Locale


object LocaleUtils {
    fun getUserPhoneLanguage(context: Context): String {
        val locale: Locale =
            context.resources.configuration.locales[0]
        return locale.language
    }
}
