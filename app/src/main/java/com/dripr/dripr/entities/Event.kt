package com.dripr.dripr.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    var id: String,
    var cover: String,
    var title: String,
    var description: String,
    var address: Address,
    var category: String,
    var members: List<User?> = arrayListOf()
//    var beginningDate: Date,
//    var beginningTime: String,
//    var endingDate: Date,
//    var endingTime: String,
//    var category: String,
//    var owner: User,
) : Parcelable