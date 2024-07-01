package com.example.queimasegura.admin.fragments.home.fire.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ZipcodeIntent(
    val id: Int,
    val locationName: String,
    val zipCode: String,
    val artName: String?,
    val tronco: String?
) : Parcelable
