package com.example.letsgo.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.letsgo.db.models.BluetoothItem
import com.example.letsgo.db.models.UbicacionDB

@Dao
interface UbicacionDBDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bluetoothItem: UbicacionDB)

    @Query("select * FROM ubicacionDB")
    fun getAll():List<UbicacionDB>

    @Query("DELETE FROM ubicacionDB")
    fun clearAll()

}