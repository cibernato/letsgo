package com.example.letsgo.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BluetoothItem")
data class BluetoothItem (
    @PrimaryKey
    var mac: String = "",
    @ColumnInfo(name = "nombre")
    var nombre: String? = "",
    @ColumnInfo(name = "fecha")
    var fecha: String? = ""
)