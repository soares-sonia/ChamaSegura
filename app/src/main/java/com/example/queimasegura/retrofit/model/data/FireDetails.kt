package com.example.queimasegura.retrofit.model.data

data class FireData(
    val id: String,
    val date: String,
    val status: String,
    val latlng: String,
    val observations: String
)

data class ReasonData(
    val namePt: String,
    val nameEn: String
)

data class ZipCodeData(
    val zipCode: String,
    val locationName: String,
    val artName: String?,
    val tronco: String?
)

data class PermissionsData(
    val icnfPermitted: Boolean,
    val icnfIdentification: String,
    val managerPermitted: Boolean
)

data class FireDetails(
    val fire: FireData,
    val zipCode: ZipCodeData,
    val reason: ReasonData,
    val permissions: PermissionsData?
)
