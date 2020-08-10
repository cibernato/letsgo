package com.example.letsgo.util

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.letsgo.R
import com.google.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Context.loge(message: String, ex: Exception = Exception()) {
    Log.e("ERRROR", message, ex)
}

fun distancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371e3 // metres
    val p1 = lat1 * Math.PI / 180; // φ, λ in radians
    val p2 = lat2 * Math.PI / 180
    val a1 = (lat2 - lat1) * Math.PI / 180
    val a2 = (lon2 - lon1) * Math.PI / 180

    val a = sin(a1 / 2) * sin(a1 / 2) +
            cos(p1) * cos(p2) *
            sin(a2 / 2) * sin(a2 / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c// in metres
}

fun Date.toISOString(): String {
    return SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss", Locale.getDefault()).format(this)
}

fun AppCompatActivity.ocultarFab() {
    findViewById<View>(R.id.fab)?.visibility = View.GONE
}

fun Fragment.mostrarFab() {
    activity?.findViewById<View>(R.id.fab)?.visibility = View.VISIBLE
}

fun Fragment.getToolbar(): Toolbar {
    return activity?.findViewById(R.id.toolbar)!!
}
var lastSin = 0.0
var lastCos = 0.0


var alpha = 0.8
private fun lowPassDegreesFilter(azimuthRadians: Float): Float {
    lastSin = alpha * lastSin + (1 - alpha) * sin(azimuthRadians)
    lastCos = alpha * lastCos + (1 - alpha) * cos(azimuthRadians)
    return ((
            Math.toDegrees(
                atan2(lastSin, lastCos)
            )+ 360) % 360).toFloat()
}