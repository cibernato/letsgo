package com.example.letsgo.ui.configuracion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivity
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.constantes.Estado
import com.example.letsgo.service.ServiceBlutooth
import com.example.letsgo.service.TrackingService
import kotlinx.android.synthetic.main.fragment_configuracion.*


class ConfiguracionFragment : Fragment() {

    val vm by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configuracion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(vm.bluetoothService == Estado.ACTIVO){
            tracking_bluettoth.isChecked = true
        }
        if(vm.gpsService == Estado.ACTIVO){
            tracking_gps.isChecked = true
        }
//        tracking_bluettoth.setOnCheckedChangeListener{btw, isChecked ->
//            if(tracking_bluettoth.isChecked){
//                activity?.stopService(Intent(context, ServiceBlutooth::class.java))
//                vm.bluetoothService = Estado.INACTIVO
//                tracking_bluettoth.isChecked = false
//            }else{
//                (activity as MainActivity).activarBluetoothService()
//                vm.bluetoothService = Estado.ACTIVO
//                tracking_bluettoth.isChecked = true
//            }
//        }
        tracking_bluettoth.setOnClickListener{
            if(!tracking_bluettoth.isChecked){
                activity?.stopService(Intent(context, ServiceBlutooth::class.java))
                vm.bluetoothService = Estado.INACTIVO
                tracking_bluettoth.isChecked = false
            }else{
                (activity as MainActivity).activarBluetoothService()
                vm.bluetoothService = Estado.ACTIVO
                tracking_bluettoth.isChecked = true
            }
        }
        tracking_gps.setOnCheckedChangeListener{ btw, isChecked ->
            if(!tracking_gps.isChecked){
                activity?.stopService(Intent(context, TrackingService::class.java))
                vm.gpsService = Estado.INACTIVO
                tracking_gps.isChecked = false
            }else{
                activity?.startService(Intent(context, TrackingService::class.java))
                vm.gpsService = Estado.ACTIVO
                tracking_gps.isChecked = true
            }
        }
    }
}