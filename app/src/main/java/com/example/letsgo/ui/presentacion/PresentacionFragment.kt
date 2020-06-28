package com.example.letsgo.ui.presentacion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.letsgo.R
import com.example.letsgo.models.Ubicacion
import kotlinx.android.synthetic.main.fragment_presentacion.*
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

        return inflater.inflate(R.layout.fragment_presentacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
            .into(presentacion_ubicacion)
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
                        currImageIndex++
                        if (currImageIndex > ubicacion.imagenes!!.size) {
                            currImageIndex = 0
                        }
                        Glide.with(requireContext())
                            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
                            .into(presentacion_ubicacion)
                    } else {
                        currImageIndex--
                        if (currImageIndex < 0) {
                            currImageIndex = ubicacion.imagenes!!.size
                        }
                        Glide.with(requireContext())
                            .load(ubicacion.imagenes!![(currImageIndex % ubicacion.imagenes!!.size)])
                            .into(presentacion_ubicacion)
                    }
                    Log.e("gForce", "$gForce")
                    Log.e(
                        "valores",
                        "${linear_acceleration[0]}, ${linear_acceleration[1]}, ${linear_acceleration[2]}, "
                    )
                    descripcion_ubicacion.text = currImageIndex.toString()
                    gravity[0] = 0f
                    gravity[1] = 0f
                    gravity[2] = 0f

                }
            }

            else -> {
            }
        }
    }


}