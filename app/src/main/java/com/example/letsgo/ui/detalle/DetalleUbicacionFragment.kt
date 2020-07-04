package com.example.letsgo.ui.detalle

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.adapter.ViewPagerStateAdapter
import com.example.letsgo.constantes.GPS_PERMISION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_detalle_ubicacion.*

class DetalleUbicacionFragment : Fragment(), OnMapReadyCallback, SensorEventListener {

    private lateinit var viewModel: MainActivityViewModel
    lateinit var adapter: ViewPagerStateAdapter
    var tipo = 0
    val vm by activityViewModels<MainActivityViewModel>()
    lateinit var sm: SensorManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        tipo = arguments?.getInt("tipo") ?: 0
        return inflater.inflate(R.layout.fragment_detalle_ubicacion, container, false)
    }

    private lateinit var mMapView: MapView
    lateinit var googleMap1: GoogleMap
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val ZOOM_LEVEL = 15f
    val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    var lastMarker: Marker? = null

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        if(vm.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            guideline.setGuidelinePercent(1f)
        }
        mMapView = mapView2
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
        adapter = ViewPagerStateAdapter(childFragmentManager)
        adapter.addFrag(NearByFragment.newInstance(tipo), "Cercanos")
        adapter.addFrag(RecomendadoFragment.newInstance(tipo), "Recomendados")
        view_pager_detalle.adapter = adapter
        view_pager_detalle.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(view_pager_detalle)
        sm = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.registerListener(
            this,
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )

    }

    var marker: Marker? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        googleMap1 = googleMap
        googleMap1.setOnMapClickListener {
//            if (lastMarker != null) lastMarker!!.remove()
//            lastMarker = googleMap1.addMarker(MarkerOptions().position(it))
        }

        with(googleMap) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), GPS_PERMISION
                )
            }
            isMyLocationEnabled = true
            mFusedLocationClient.lastLocation.addOnSuccessListener {
            }
            moveCamera(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        -16.4032661,
                        -71.5476593
                    ), 14.08f
                )
            )
            vm.ubicaciones.filter { it.tipo == tipo }.forEach {
                addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.posicion?.latitude!!,
                            it.posicion?.longitude!!
                        )
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }


    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if (event.values[0] < -9 || event.values[0] > 9) {
                vm.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (event.values[1] < -9 || event.values[1] > 9) {
                vm.orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}
