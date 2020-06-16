package com.example.letsgo.ui.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.constantes.GPS_PERMISION
import com.example.letsgo.constantes.TiposLocales
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_mapa.*

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        return inflater.inflate(R.layout.fragment_mapa, container, false)
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
        mMapView = mapView
        mMapView.onCreate(mapViewBundle)

        mMapView.getMapAsync(this)
        button.setOnClickListener {
            findNavController().navigate(R.id.nav_detalleUbicacion, bundleOf("tipo" to TiposLocales.AGENCIAS.ordinal))
        }
        button2.setOnClickListener {
            findNavController().navigate(R.id.nav_detalleUbicacion, bundleOf("tipo" to TiposLocales.RESTAURANTE.ordinal))
        }
        button3.setOnClickListener {
            findNavController().navigate(R.id.nav_detalleUbicacion, bundleOf("tipo" to TiposLocales.TURISTICO.ordinal))
        }


    }
    var marker :Marker?=null
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        googleMap1 = googleMap
        googleMap1.setOnMapClickListener {
//            if (lastMarker != null) lastMarker!!.remove()
//            lastMarker = googleMap1.addMarker(MarkerOptions().position(it))
        }
        googleMap1.setOnMarkerClickListener{
            findNavController().navigate(R.id.nav_detalleUbicacion)
            true
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
                Log.e("location ", "$it ")
                if (it != null) {
                    moveCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), ZOOM_LEVEL
                        )
                    )
                    marker = addMarker(MarkerOptions().position(
                        LatLng(it.latitude+0.03,
                            it.longitude+0.03)
                    ))
                    marker
                }
            }
            addMarker(MarkerOptions().position(LatLng(-16.375413,71.597465)))
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
}
