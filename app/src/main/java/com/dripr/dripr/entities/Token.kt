package com.dripr.dripr.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Token(val access: String, val token: String): Parcelable {
    override fun toString(): String {
        return this.token
    }
}