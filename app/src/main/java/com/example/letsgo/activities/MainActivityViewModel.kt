package com.example.letsgo.activities

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.letsgo.Ubicacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {

    val ref = FirebaseFirestore.getInstance().collection("/ubicaciones")
    var ubicaciones = ArrayList<Ubicacion>()

    fun getUbicaciones() {
        viewModelScope.launch(Dispatchers.IO) {
            ref.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                ubicaciones.clear()
                querySnapshot?.documents?.forEach {
                    ubicaciones.add(it.toObject(Ubicacion::class.java) ?: Ubicacion())
                }
                Log.e("ubicaciones","${ubicaciones.size}")
            }
        }
    }

}