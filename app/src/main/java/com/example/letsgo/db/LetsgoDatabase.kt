package com.example.letsgo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.letsgo.db.dao.BluetoothItemDao
import com.example.letsgo.db.dao.UbicacionDBDao
import com.example.letsgo.db.models.BluetoothItem
import com.example.letsgo.db.models.UbicacionDB

@Database(
    entities = [
        BluetoothItem::class,
        UbicacionDB::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LetsgoDatabase : RoomDatabase() {

    abstract val bluetoothItemDao: BluetoothItemDao
    abstract val ubicacionDBDao: UbicacionDBDao

    companion object {
        @Volatile
        private var INSTANCE: LetsgoDatabase? = null

        fun getInstance(context: Context): LetsgoDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LetsgoDatabase::class.java,
                        "letsgo_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}