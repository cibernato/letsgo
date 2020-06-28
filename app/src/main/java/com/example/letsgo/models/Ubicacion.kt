package com.example.letsgo.models

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class Ubicacion(
    var tipo: Int? = 0,
    var posicion: @RawValue GeoPoint? = null,
    var imagenes: @RawValue List<String>? = null,
    var descripcion: String? = "",
    var macAsociada: String? = "",
    var nombre : String?=""
) : Parcelable {
}