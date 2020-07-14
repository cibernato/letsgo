package com.example.letsgo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.letsgo.db.LetsgoDatabase
import com.example.letsgo.db.models.UbicacionDB
import com.example.letsgo.util.toISOString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class TrackingService  : Service() {

    lateinit var locationManager: LocationManager
    lateinit var db : LetsgoDatabase
    var lastLatitud = 0.0
    var lastLongitud = 0.0
    val a2 = 0.95
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    var f=0

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        db = LetsgoDatabase.getInstance(this)
        Log.e("TrackingService", "Servicio iniciado")
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            5L,
            100f,
            object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    location?.let {
                        if(f==0){
                            lastLongitud = it.longitude
                            lastLatitud=it.latitude
                            f++
                        }else{
                            lastLatitud = a2 * lastLatitud + (1f - a2) * it.latitude
                            lastLongitud = a2 * lastLongitud + (1f - a2) * it.longitude
                        }

                        uiScope.launch {
                            val u = UbicacionDB(latitud = it.latitude,longitud = it.longitude,id = 0,fecha = Date().toISOString())
                            db.ubicacionDBDao.insert(u)
                            Log.e("UbicacionDB","${u.latitud},${u.longitud}")
                        }

//                        FirebaseFirestore.getInstance().collection("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/gpsTracking")
//                            .add( hashMapOf(
//                                "ubicacion" to GeoPoint(it.latitude,it.longitude),
//                                "timestamp" to Date().toISOString()
//                            ))
                    }
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    Log.e("onStatusChanged", "onStatusChanged xd")
                }

                override fun onProviderEnabled(provider: String?) {
                    Log.e("onProviderEnabled", "onProviderEnabled $provider")
                }

                override fun onProviderDisabled(provider: String?) {
                    Log.e("onProviderDisabled", "onProviderDisabled xd")
                }
            })
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TrackingService", "Servicio cerrado")
    }
}