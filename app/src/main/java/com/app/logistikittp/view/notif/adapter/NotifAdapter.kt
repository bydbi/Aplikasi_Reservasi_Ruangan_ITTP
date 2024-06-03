package com.app.logistikittp.view.notif.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.logistikittp.R
import com.app.logistikittp.data.model.Notifikasi
import com.app.logistikittp.databinding.CustomListNotifikasiBinding

class NotifAdapter(private val context: Context, private val notifikasiList: MutableList<Notifikasi>) :
    RecyclerView.Adapter<NotifAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomListNotifikasiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CustomListNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notifikasi = notifikasiList[position]
        if (notifikasi.status == "ditolak") {
            holder.binding.tvStatus.text = "Ditolak!"
            holder.binding.tvStatus.setTextColor(Color.parseColor("#404040"))
        } else {
            holder.binding.tvStatus.text = "Diterima!"
            holder.binding.tvStatus.setTextColor(Color.parseColor("#71C4EF"))
        }
        holder.binding.tvDeskripsi.text = notifikasi.message
        holder.binding.rlNotifikasi.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("idBooking", notifikasi.idBooking)
            Navigation.findNavController(holder.binding.root).navigate(R.id.action_notifFragment_to_detailHistoriFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return notifikasiList.size
    }

    fun updateData(newData: List<Notifikasi>) {
        notifikasiList.clear()
        notifikasiList.addAll(newData)
        notifyDataSetChanged()
    }

}
