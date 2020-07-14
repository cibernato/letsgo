package com.example.letsgo.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicacionDB")
class UbicacionDB(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "latitud")
    var latitud: Double? = 0.0,

    @ColumnInfo(name = "longitud")
    var longitud: Double? = 0.0,

    @ColumnInfo(name = "fecha")
    var fecha: String? = ""

)