package com.app.logistikittp.view.history.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.databinding.FragmentDetailHistoryAdminBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailArsipAdminFragment : Fragment() {
    lateinit var binding : FragmentDetailHistoryAdminBinding
    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailHistoryAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        val idBooking = arguments?.getString("idBooking")
        val userId = arguments?.getString("userId")
        val gedung = arguments?.getString("gedung")
        val ruangan = arguments?.getString("ruangan")
        val tanggal_pinjam = arguments?.getString("tanggal_pinjam")
        val waktu_pinjam = arguments?.getString("waktu_pinjam")
        val status = arguments?.getString("status")
        val peminjam = arguments?.getString("peminjam")
        val asal_peminjam = arguments?.getString("asal_peminjam")
        val nim_nik = arguments?.getString("nim_nik")
        val nomor_hp = arguments?.getString("nomor_hp")
        val keterangan_acara = arguments?.getString("keterangan_acara")
        val surat_izin = arguments?.getString("surat_izin")
        Log.d("check booking: ", idBooking.toString())

        binding.apply {
            tvAsalPeminjam.text = asal_peminjam
            tvNamaPeminjam.text = peminjam
            tvNimNik.text = nim_nik
            tvNomorTelepon.text = nomor_hp
            tvAcara.text = keterangan_acara
            tvTanggalAcara.text = tanggal_pinjam
            tvWaktuAcara.text = waktu_pinjam
            btnSuratIzin.setOnClickListener {
                val pdfUrl = surat_izin
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                startActivity(intent)
            }
            binding.btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }


    private fun convertTanggal(tanggal: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(tanggal ?: "") ?: Date()
        return outputFormat.format(date)
    }
}