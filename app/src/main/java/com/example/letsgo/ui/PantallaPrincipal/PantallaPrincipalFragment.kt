package com.example.letsgo.ui.PantallaPrincipal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letsgo.R
import com.example.letsgo.util.getToolbar
import kotlinx.android.synthetic.main.fragment_pantalla_principal.*


class PantallaPrincipalFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getToolbar().visibility = View.GONE
        return inflater.inflate(R.layout.fragment_pantalla_principal, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActicardId.setOnClickListener {
            findNavController().navigate(R.id.nav_mapa)
        }
        TaskCardId.setOnClickListener {
            findNavController().navigate(R.id.nav_lectorQrFragment)
        }
        PerfilCardId.setOnClickListener {
            findNavController().navigate(R.id.nav_estadisticasFragment)
        }
        AcercaCardId.setOnClickListener {
            findNavController().navigate(R.id.nav_configuracion)
        }
    }

    override fun onStop() {
        super.onStop()
        getToolbar().visibility = View.VISIBLE
    }

}