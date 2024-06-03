package com.app.logistikittp.view.history.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentDetailHistoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailHistoryFragment : Fragment() {
    lateinit var binding : FragmentDetailHistoryBinding
    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        val idBooking = arguments?.getString("idBooking")
        lifecycleScope.launch {
            sharedPref.getUserId.collect { userId ->
                loadDataBooking(idBooking!!, userId)
            }
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun loadDataBooking(bookingId: String,userId : String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("booking").child(userId).child(bookingId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking != null) {
                    binding.apply {
                        tvAsalPeminjam.text = booking.asal_peminjam
                        tvNamaPeminjam.text = booking.peminjam
                        tvNimNik.text =  booking.nim_nik
                        tvNomorTelepon.text =  booking.nomor_hp
                        tvAcara.text =  booking.keterangan_acara
                        tvTanggalAcara.text =  convertTanggal(booking.tanggal_pinjam)
                        tvWaktuAcara.text =  booking.waktu_pinjam
                        btnSuratIzin.setOnClickListener {
                            val pdfUrl =  booking.surat_izin
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun convertTanggal(tanggal: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(tanggal ?: "") ?: Date()
        return outputFormat.format(date)
    }
}