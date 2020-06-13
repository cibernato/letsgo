package com.example.letsgo.ui.detalle

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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

class DetalleUbicacionFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainActivityViewModel
    lateinit var adapter : ViewPagerStateAdapter
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProvider(this).get(MainActivityViewModel::class.java)
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
        mMapView = mapView2
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
        adapter = ViewPagerStateAdapter(childFragmentManager)
        adapter.addFrag(NearByFragment(),"Cercanos")
        adapter.addFrag(RecomendadoFragment(),"Recomendados")
        view_pager_detalle.adapter = adapter
        view_pager_detalle.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(view_pager_detalle)


    }
    var marker : Marker?=null
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
                }
            }
        }
    }
}
