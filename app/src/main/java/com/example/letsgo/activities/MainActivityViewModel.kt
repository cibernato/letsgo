package com.example.letsgo.activities

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letsgo.models.Ubicacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    val ref = FirebaseFirestore.getInstance().collection("/ubicaciones")
    var ubicaciones = ArrayList<Ubicacion>()

    var orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    fun getUbicaciones() {
        viewModelScope.launch(Dispatchers.IO) {
            ref.addSnapshotListener { querySnapshot, _ ->
                ubicaciones.clear()
                querySnapshot?.documents?.forEach {
                    ubicaciones.add(it.toObject(Ubicacion::class.java) ?: Ubicacion())
                }
                Log.e("ubicaciones","${ubicaciones.size}")
            }
        }
    }

}