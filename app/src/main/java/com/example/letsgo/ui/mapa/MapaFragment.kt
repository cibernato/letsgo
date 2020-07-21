package com.example.letsgo.ui.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.constantes.Estado
import com.example.letsgo.constantes.GPS_PERMISION
import com.example.letsgo.constantes.TiposLocales
import com.example.letsgo.service.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_mapa.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainActivityViewModel
    val vm by activityViewModels<MainActivityViewModel>()

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
            findNavController().navigate(
                R.id.nav_detalleUbicacion,
                bundleOf("tipo" to TiposLocales.AGENCIAS.ordinal)
            )
        }
        button2.setOnClickListener {
            findNavController().navigate(
                R.id.nav_detalleUbicacion,
                bundleOf("tipo" to TiposLocales.RESTAURANTE.ordinal)
            )
        }
        button3.setOnClickListener {
            findNavController().navigate(
                R.id.nav_detalleUbicacion,
                bundleOf("tipo" to TiposLocales.TURISTICO.ordinal)
            )
        }

        startTrackingService()
    }

    var marker: Marker? = null
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        googleMap1 = googleMap
        googleMap1.setOnMapClickListener {
//            if (lastMarker != null) lastMarker!!.remove()
//            lastMarker = googleMap1.addMarker(MarkerOptions().position(it))
        }
        googleMap1.setOnMarkerClickListener {
            vm.ubicaciones.find { u -> u.posicion!!.latitude == it.position.latitude && u.posicion!!.longitude == it.position.longitude }
                ?.let { ub ->
                    Log.e("error", "onMapReady: ${Gson().toJson(ub)}" )
                    findNavController().navigate(
                        R.id.nav_detalleUbicacion,
                        bundleOf("tipo" to ub.tipo)
                    )
                }

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
                    marker = addMarker(
                        MarkerOptions().position(
                            LatLng(
                                it.latitude + 0.03,
                                it.longitude + 0.03
                            )
                        )
                    )
                    marker
                }
            }
            moveCamera(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        -16.4032661,
                        -71.5476593
                    ), 14.08f
                )
            )
            vm.ubicaciones.forEach {
                addMarker(
                    MarkerOptions().position(
                        LatLng(
                            it.posicion!!.latitude,
                            it.posicion!!.longitude
                        )
                    ).snippet(it.nombre)
                )
            }
        }
    }

    private fun startTrackingService() {
        vm.viewModelScope.launch(Dispatchers.IO) {
            val ubs = vm.db.ubicacionDBDao.getAll()
            if (ubs.size > 1) {
                var distancia = 0.0
                for (i in 1 until ubs.size) {
                    val antes = ubs[i - 1]
                    val despues = ubs[i]
                    distancia += com.example.letsgo.util.distancia(
                        antes.latitud!!,
                        antes.longitud!!,
                        despues.latitud!!,
                        despues.longitud!!
                    )
                }
                val fecha = ubs.last().fecha?.split("T")?.get(0)
                Log.e("Antes de subir", "distancia: ,\n ${distancia},  \n $fecha  ")

                FirebaseFirestore.getInstance()
                    .document("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/gpsTracking/$fecha")
                    .update(
                        "distancia", FieldValue.increment(distancia)
                    ).addOnFailureListener {
                        FirebaseFirestore.getInstance()
                            .collection("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/gpsTracking")
                            .document("$fecha")
                            .set(
                                hashMapOf(
                                    "fecha" to fecha,
                                    "distancia" to distancia
                                )
                            )
                    }

                vm.db.ubicacionDBDao.clearAll()
            }
            activity?.runOnUiThread {
                if (vm.gpsService != Estado.ACTIVO) {
                    activity?.startService(Intent(requireContext(), TrackingService::class.java))
                    vm.gpsService = Estado.ACTIVO
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (this::mMapView.isInitialized) {
            mMapView.onStop()
        }
    }


    override fun onPause() {
        if (this::mMapView.isInitialized) {
            mMapView.onPause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (this::mMapView.isInitialized) {
            mMapView.onDestroy()
        }
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (this::mMapView.isInitialized) {
            mMapView.onLowMemory()
        }
    }
}
