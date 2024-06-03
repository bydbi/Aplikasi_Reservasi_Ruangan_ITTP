package com.app.logistikittp.view.history.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.logistikittp.data.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Bundle
import androidx.navigation.Navigation
import com.app.logistikittp.R
import com.app.logistikittp.databinding.CustomListHistoryBinding

class ArsipAdminAdapter(private val context: Context, private var historiList: MutableList<Booking>) :
    RecyclerView.Adapter<ArsipAdminAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomListHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CustomListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderHistory = historiList[position]
        holder.binding.tvAsalPeminjam.text = orderHistory.asal_peminjam
        holder.binding.tvRuangan.text = "${orderHistory.gedung!!.toUpperCase()} ${orderHistory.ruangan}"
        //Kamis, 2 November 2023
        val convertWaktu = convertTanggal(orderHistory.tanggal_pinjam)
        holder.binding.tvTanggal.text = convertWaktu
        holder.binding.tvWaktu.text = orderHistory.waktu_pinjam
        holder.binding.rlRiwayat.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("idBooking", orderHistory.idBooking)
            bundle.putString("userId", orderHistory.userId)
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
            Navigation.findNavController(holder.binding.root).navigate(R.id.action_historiAdminFragment_to_detailHistoryAdminFragment, bundle)
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
