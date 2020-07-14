package com.example.letsgo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.letsgo.R
import com.example.letsgo.models.Ubicacion
import kotlinx.android.synthetic.main.item_ubicacion_detalle.view.*

class CercanosAdapter(
    var listaUbs: List<Ubicacion>,
    var cercanoListener: CercanoListener
) : RecyclerView.Adapter<CercanosAdapter.MyViewHolder>() {
    interface CercanoListener{
        fun onCercanoClick(ubicacion: Ubicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context!!)
                .inflate(R.layout.item_ubicacion_detalle, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listaUbs.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listaUbs[position])
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(ubicacion: Ubicacion) {
            Glide.with(itemView.context!!).load(ubicacion.imagenes!![0]).into(itemView.icono_lugar)
            itemView.titulo_lugar.text = ubicacion.nombre
            itemView.ratingBar.rating = ubicacion.valoracion?.toFloat() ?: 1f
            itemView.descripcion_lugar.text = ubicacion.direccion
            itemView.setOnClickListener{
                cercanoListener.onCercanoClick(ubicacion)
            }

        }
    }
}