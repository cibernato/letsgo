package com.example.letsgo.ui.presentacion

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.letsgo.R
import com.example.letsgo.models.Ubicacion
import com.example.letsgo.util.getToolbar
import com.example.letsgo.util.mostrarFab
import kotlinx.android.synthetic.main.fragment_presentacion.*
import java.util.*
import kotlin.math.sqrt


class PresentacionFragment : Fragment(), SensorEventListener {

    lateinit var ubicacion: Ubicacion
    lateinit var sm: SensorManager
    var currImageIndex = 0
    var SHAKE_SLOP_TIME_MS = 500
    var SHAKE_THRESHOLD_GRAVITY = 2.7f
    var mShakeTimestamp = 0L
    val gravity = FloatArray(3)
    val linear_acceleration = FloatArray(3)
    val SPEECH_REQUEST_CODE = 6548

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ubicacion = arguments?.getParcelable<Ubicacion>("ubicacion") as Ubicacion

        sm = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(
            this,
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        mostrarFab()
        getToolbar().title = ubicacion.nombre
        return inflater.inflate(R.layout.fragment_presentacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
            .into(presentacion_ubicacion)
        activity?.findViewById<View>(R.id.fab)?.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }
        descripcion_ubicacion.text = "${ubicacion.descripcion}".toUpperCase(Locale.getDefault())
        nombre_ubicacion.text = ubicacion.nombre

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event!!.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = (event.values[0])
                val y = event.values[1]
                val z = event.values[2]
                val gX = x / SensorManager.GRAVITY_EARTH
                val gY = y / SensorManager.GRAVITY_EARTH
                val gZ = z / SensorManager.GRAVITY_EARTH
                val gForce: Float = sqrt(gX * gX + gY * gY + gZ * gZ)

                if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                    val now = System.currentTimeMillis()
                    if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                        return
                    }
                    mShakeTimestamp = now
                    val alpha = 0.8f
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                    linear_acceleration[0] = event.values[0] - gravity[0]
                    linear_acceleration[1] = event.values[1] - gravity[1]
                    linear_acceleration[2] = event.values[2] - gravity[2]
                    if (linear_acceleration[0] < 0) {
                        loadNext()
                    } else {
                        loadPrevious()
                    }
                    Log.e("gForce", "$gForce")
                    Log.e(
                        "valores",
                        "${linear_acceleration[0]}, ${linear_acceleration[1]}, ${linear_acceleration[2]}, "
                    )
                    gravity[0] = 0f
                    gravity[1] = 0f
                    gravity[2] = 0f

                }
            }

            else -> {
            }
        }
    }

    fun loadNext() {
        currImageIndex++
        if (currImageIndex > ubicacion.imagenes!!.size) {
            currImageIndex = 0
        }
        Glide.with(requireContext())
            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
            .into(presentacion_ubicacion)
    }

    fun loadPrevious() {
        currImageIndex--
        if (currImageIndex < 0) {
            currImageIndex = ubicacion.imagenes!!.size
        }
        Glide.with(requireContext())
            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
            .into(presentacion_ubicacion)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SPEECH_REQUEST_CODE -> {
                val results: List<String> =
                    data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    ) ?: emptyList()
                if (results.isNotEmpty()) {
                    when (results[0]) {
                        "siguiente", "next", "sigue", "despues", "adelante" -> loadNext()
                        "antes", "anterior", "atras", "back", "go back" -> loadPrevious()
                        else -> Toast.makeText(
                            requireContext(),
                            "Pruebe con siguente,anterior o palabras similares.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


}