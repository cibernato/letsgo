package com.example.letsgo.ui.estadisticas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.models.Estadistica
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.estadistica_item.view.*
import kotlinx.android.synthetic.main.fragment_estadisticas.*


class EstadisticasFragment : Fragment() {

    lateinit var firestoreRecyclerAdapter: FirestoreRecyclerAdapter<Estadistica, EstadisticaViewHolder>
    val vm by activityViewModels<MainActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_estadisticas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        distancias_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val ref = FirebaseFirestore.getInstance().collection("/usuarios/${FirebaseAuth.getInstance().currentUser?.uid}/gpsTracking")
        val option = FirestoreRecyclerOptions.Builder<Estadistica>()
            .setQuery(ref,Estadistica::class.java)
            .build()
        firestoreRecyclerAdapter =
            object : FirestoreRecyclerAdapter<Estadistica, EstadisticaViewHolder>(option) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): EstadisticaViewHolder {
                    return EstadisticaViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.estadistica_item, parent, false)
                    )
                }

                override fun onBindViewHolder(
                    holder: EstadisticaViewHolder,
                    position: Int,
                    model: Estadistica
                ) {
                    holder.bind(model)
                }
            }
        firestoreRecyclerAdapter.startListening()
        distancias_recycler_view.adapter = firestoreRecyclerAdapter
    }


    inner class EstadisticaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(estadistica: Estadistica) {
            itemView.distancia_estadistica.text = if(estadistica.distancia!! <1000) "%.2f m.".format(estadistica.distancia) else "%.2f Km.".format(estadistica.distancia!!/1000)
            itemView.fecha_estadistica.text = estadistica.fecha
        }
    }
}

