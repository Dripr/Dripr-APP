package com.dripr.dripr.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Address(
    var placeName : String,
    var latitude: Double,
    var longitude: Double
) : Parcelable