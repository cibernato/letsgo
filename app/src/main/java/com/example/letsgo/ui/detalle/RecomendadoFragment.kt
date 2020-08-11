package com.example.letsgo.ui.detalle

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.adapter.CercanosAdapter
import com.example.letsgo.constantes.RADIO_MINIMO
import com.example.letsgo.models.Ubicacion
import com.example.letsgo.util.distancia
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_recomendado.*

class RecomendadoFragment : Fragment(), CercanosAdapter.CercanoListener {
    var tipo = 0
    lateinit var adapter: CercanosAdapter
    val vm by activityViewModels<MainActivityViewModel>()
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recomendado, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            Log.e("Entra csmr", "${loc}")
            if (loc != null) {
                val filtrados = vm.ubicaciones.value!!.filter {
                    it.tipo == tipo && distancia(
                        loc.latitude,
                        loc.longitude,
                        it.posicion!!.latitude,
                        it.posicion!!.longitude
                    ) < RADIO_MINIMO
                }

                recomendados.layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL, false
                )
                adapter =
                    if (!filtrados.isEmpty()) CercanosAdapter(filtrados, this) else CercanosAdapter(
                        vm.ubicaciones.value!!.filter { it.tipo == tipo }.take(2),
                        this
                    )
                recomendados.adapter = adapter

            } else {
                recomendados.layoutManager = LinearLayoutManager(
                    requireContext(),
                    RecyclerView.VERTICAL, false
                )
                adapter =
                    CercanosAdapter(vm.ubicaciones.value!!.filter { it.tipo == tipo }.take(2), this)
                recomendados.adapter = adapter

            }
        }
    }

    companion object {
        fun newInstance(tipo: Int) = RecomendadoFragment().apply { this.tipo = tipo }
    }

    override fun onCercanoClick(ubicacion: Ubicacion) {
        (activity?.supportFragmentManager?.primaryNavigationFragment!!.childFragmentManager.fragments[0] as DetalleUbicacionFragment).disableSensor()
        findNavController().navigate(
            R.id.nav_presentacionFragment,
            bundleOf("ubicacion" to ubicacion)
        )
    }

}