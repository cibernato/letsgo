package com.example.letsgo.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel

class RecomendadoFragment : Fragment() {
    var tipo = 0
    val vm by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recomendado, container, false)
    }

    companion object{
        fun newInstance(tipo:Int)= RecomendadoFragment().apply { this.tipo  = tipo}
    }

}