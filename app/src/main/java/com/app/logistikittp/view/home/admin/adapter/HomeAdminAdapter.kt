package com.app.logistikittp.view.home.admin.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.logistikittp.R
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.CustomListPeminjamanBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdminAdapter(private val context: Context, private val historiList: MutableList<Booking>) :
    RecyclerView.Adapter<HomeAdminAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomListPeminjamanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CustomListPeminjamanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderHistory = historiList[position]
        if (orderHistory.status == "pending") {
            holder.binding.tvGedung.text = "Gedung ${orderHistory.gedung!!.toUpperCase()}"
            holder.binding.tvRuangan.text = "Ruangan ${orderHistory.gedung!!.toUpperCase()} ${orderHistory.ruangan}"
            //Kamis, 2 November 2023
            val convertWaktu = convertTanggal(orderHistory.tanggal_pinjam)
            holder.binding.tvTanggal.text = convertWaktu
            holder.binding.rlRiwayat.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("idBooking", orderHistory.idBooking)
                bundle.putString("userId", orderHistory.userId)
                bundle.putString("tanggalOri", orderHistory.tanggal_pinjam)
                bundle.putString("gedung", orderHistory.gedung)
                bundle.putString("ruangan", orderHistory.ruangan)
                bundle.putString("tanggal_pinjam", convertWaktu)
                bundle.putString("waktu_pinjam", orderHistory.waktu_pinjam)
                bundle.putString("status", orderHistory.status)
                bundle.putString("peminjam", orderHistory.peminjam)
                bundle.putString("asal_peminjam", orderHistory.asal_peminjam)
                bundle.putString("nim_nik", orderHistory.nim_nik)
                bundle.putString("nomor_hp", orderHistory.nomor_hp)
                bundle.putString("keterangan_acara", orderHistory.keterangan_acara)
                bundle.putString("surat_izin", orderHistory.surat_izin)
                Navigation.findNavController(holder.binding.root).navigate(R.id.action_homeAdminFragment_to_prosesPeminjamanFragment, bundle)
            }
        } else {
            holder.binding.root.visibility = View.GONE
            holder.binding.root.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }


    override fun getItemCount(): Int {
        return historiList.size
    }

    fun updateData(newData: List<Booking>) {
        historiList.clear()
        historiList.addAll(newData)
        notifyDataSetChanged()
    }

    private fun convertTanggal(tanggal: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(tanggal ?: "") ?: Date()
        return outputFormat.format(date)
    }
}
