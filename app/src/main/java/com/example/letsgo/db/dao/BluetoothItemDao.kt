package com.example.letsgo.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.letsgo.db.models.BluetoothItem

@Dao
interface BluetoothItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bluetoothItem: BluetoothItem)

    @Query("SELECT * from BluetoothItem ORDER BY fecha DESC")
    fun getLastInserted(): LiveData<List<BluetoothItem>>

    @Query("DELETE FROM BluetoothItem")
    fun clearAll()

}