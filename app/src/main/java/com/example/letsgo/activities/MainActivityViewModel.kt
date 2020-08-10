package com.example.letsgo.activities

import android.content.pm.ActivityInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letsgo.constantes.Estado
import com.example.letsgo.db.LetsgoDatabase
import com.example.letsgo.models.Ubicacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    lateinit var db: LetsgoDatabase

    val ref = FirebaseFirestore.getInstance().collection("/ubicaciones")

    var orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    var gpsService = Estado.INACTIVO
    var bluetoothService = Estado.INACTIVO

    private var _ubicaciones = MutableLiveData<ArrayList<Ubicacion>>().apply {
        value = ArrayList()
    }
    var ubicaciones: LiveData<ArrayList<Ubicacion>> = _ubicaciones
    fun getUbicaciones() {
        viewModelScope.launch(Dispatchers.IO) {
            ref.addSnapshotListener { querySnapshot, _ ->
                val temp =querySnapshot?.documents?.map {
                    it.toObject(Ubicacion::class.java) ?: Ubicacion()
                } ?: ArrayList()
                _ubicaciones.postValue(ArrayList<Ubicacion>().apply {
                    addAll(temp)
                })
            }
        }
    }

}