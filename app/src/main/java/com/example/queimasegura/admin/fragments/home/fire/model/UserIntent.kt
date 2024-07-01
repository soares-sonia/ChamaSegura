package com.example.queimasegura.admin.fragments.home.fire.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserIntent(
    val userId: String,
    val fullName: String,
    val email: String,
    val nif: String
): Parcelable
