package com.app.logistikittp.view.history.user.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.CustomListRiwayatBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val context: Context, private val historiList: MutableList<Booking>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomListRiwayatBinding) : RecyclerView.ViewHolder(binding.root){
        val tvStatusPeminjaman: TextView = binding.tvstatusPeminjaman
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CustomListRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderHistory = historiList[position]
        holder.binding.tvAsalPeminjam.text = orderHistory.asal_peminjam
        holder.binding.tvRuangan.text = "${orderHistory.gedung!!.toUpperCase()} ${orderHistory.ruangan}"

        orderHistory.timestamp?.let {
            val date = Date(it)
            val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            holder.binding.tvWaktuPeminjaman.text = format.format(date)
        }

        val statusPeminjaman ="${orderHistory.status}"
        holder.tvStatusPeminjaman.text = statusPeminjaman

        val convertWaktu = convertTanggal(orderHistory.tanggal_pinjam)
        holder.binding.tvTanggal.text = convertWaktu
        holder.binding.tvWaktu.text = orderHistory.waktu_pinjam
        holder.binding.rlRiwayat.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("idBooking", orderHistory.idBooking)
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
            Navigation.findNavController(holder.binding.root).navigate(com.app.logistikittp.R.id.action_historiFragment_to_detailHistoriFragment, bundle)
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
