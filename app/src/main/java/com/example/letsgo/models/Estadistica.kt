package com.example.letsgo.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Estadistica(
    var distancia:Double? = 0.0,
    var fecha : String? = ""

)  : Parcelable {
}