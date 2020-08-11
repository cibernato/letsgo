package com.example.letsgo.ui.detalle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letsgo.R
import com.example.letsgo.models.Ubicacion
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.adapter.CercanosAdapter
import kotlinx.android.synthetic.main.fragment_near_by.*


class NearByFragment : Fragment(), CercanosAdapter.CercanoListener {
    var tipo = 0
    lateinit var adapter : CercanosAdapter
    val vm by activityViewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CercanosAdapter(vm.ubicaciones.value!!.filter { it.tipo == tipo },this)
        retainInstance= true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lista_cercanos.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        lista_cercanos.adapter = adapter
    }

    companion object{
        fun newInstance(tipo:Int)= NearByFragment().apply { this.tipo  = tipo}

    }

    override fun onCercanoClick(ubicacion: Ubicacion) {
        (activity?.supportFragmentManager?.primaryNavigationFragment!!.childFragmentManager.fragments[0] as DetalleUbicacionFragment).disableSensor()
        findNavController().navigate(R.id.nav_presentacionFragment, bundleOf("ubicacion" to ubicacion))
    }
}