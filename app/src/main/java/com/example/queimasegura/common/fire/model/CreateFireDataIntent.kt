package com.example.queimasegura.common.fire.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CreateFireDataIntent(
    val typeId: Int?,
    val reasonId: Int?,
    val selectedDate: String?,
    val selectedDateString: String?
) : Parcelable
