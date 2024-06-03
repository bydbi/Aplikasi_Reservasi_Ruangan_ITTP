package com.app.logistikittp.view.home.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentDetailRequestPeminjamanBinding
import com.app.logistikittp.databinding.FragmentHomeAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailRequestPeminjamanFragment : Fragment() {
    lateinit var binding : FragmentDetailRequestPeminjamanBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailRequestPeminjamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())

        val idBooking = arguments?.getString("idBooking")
        val userId = arguments?.getString("userId")
        val tanggal = arguments?.getString("tanggalOri")
        val gedung = arguments?.getString("gedung")
        val ruangan = arguments?.getString("ruangan")
        val waktu = arguments?.getString("waktu_pinjam")

        loadDataBooking(idBooking!!, userId!!)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSetuju.setOnClickListener {
            val waktuAwal = waktu?.substring(0, 5)
            val waktuAkhir = waktu?.substring(6, 11)
            val startIndex = getIndexFromWaktu(waktuAwal!!)
            val endIndex = getIndexFromWaktu(waktuAkhir!!)

            val confirmationDialog = ConfirmationDialogFragment("Anda Yakin?") {
                updateWaktuStatus(gedung!!, ruangan!!, tanggal!!, startIndex, endIndex, idBooking, userId, waktu)
            }
            confirmationDialog.show(childFragmentManager, "ConfirmationDialog")
        }

        binding.btnTolak.setOnClickListener {
            val confirmationDialog = ConfirmationDialogFragment("Anda Yakin?") {
                tolakPermintaan(userId, idBooking, gedung!!, ruangan!!, tanggal!!, waktu!!)
            }
            confirmationDialog.show(childFragmentManager, "ConfirmationDialog")
        }


    }

    private fun loadDataBooking(bookingId: String, userId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("booking").child(userId).child(bookingId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking != null) {
                    binding.apply {
                        tvAsalPeminjam.text = booking.asal_peminjam
                        tvNamaPeminjam.text = booking.peminjam
                        tvNimNik.text = booking.nim_nik
                        tvNomorTelepon.text = booking.nomor_hp
                        tvAcara.text = booking.keterangan_acara
                        tvTanggalAcara.text = convertTanggal(booking.tanggal_pinjam)
                        tvWaktuAcara.text = booking.waktu_pinjam
                        btnSuratIzin.setOnClickListener {
                            val pdfUrl = booking.surat_izin
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
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID"))
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("in", "ID"))
        val date = inputFormat.parse(tanggal ?: "") ?: Date()
        return outputFormat.format(date)
    }

    private fun createJamList(): List<String> {
        val jamList = mutableListOf<String>()
        for (hour in 7..21) {
            val jam = String.format("%02d:00", hour)
            jamList.add(jam)
        }
        Log.d("Jam List : ", jamList.toString())
        return jamList
    }

    private fun getIndexFromWaktu(waktuString: String): Int {
        val jam = waktuString.substring(0, 2).toInt()
        val index = jam - 7
        Log.d("Waktu Index : ", index.toString())
        return index
    }

    private fun updateWaktuStatus(gedung: String, ruangan: String, tanggal: String, startIndex: Int, endIndex: Int, idBooking: String, userId : String, waktu: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("gedung").child(gedung).child("ruangan").child(ruangan).child("ketersediaan").child(tanggal).child("waktu")

        for (index in startIndex..endIndex) {
            val statusRef = databaseReference.child(index.toString()).child("status")
            statusRef.setValue(false)
                .addOnSuccessListener {
                    Log.d("Update Status", "Success for index: $index")
                    terimaPermintaan(userId,idBooking)
                    kirimNotifikasi(userId, idBooking, "Ruang ${gedung} ${ruangan} berhasil dipinjam untuk, ${convertTanggal(tanggal)}, Pukul ${waktu}.", "diterima")
                    Toast.makeText(requireContext(), "Berhasil menyetujui permintaan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("Update Status", "Error updating status for index: $index", e)
                    Toast.makeText(requireContext(), "Gagal menyetujui permintaan", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun terimaPermintaan(userId : String,bookingId: String) {
        val bookingReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("booking").child(userId).child(bookingId).child("status")

        bookingReference.setValue("diterima")
            .addOnSuccessListener {
                Log.d("Update Booking Status", "Booking status updated to 'diterima'")
            }
            .addOnFailureListener { e ->
                Log.w("Update Booking Status", "Error updating booking status", e)
            }
    }

    private fun tolakPermintaan(userId : String, bookingId: String, gedung: String, ruangan: String, tanggal: String, waktu: String) {
        val bookingReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("booking").child(userId).child(bookingId).child("status")

        bookingReference.setValue("ditolak")
            .addOnSuccessListener {
                Log.d("Update Booking Status", "Booking status updated to 'ditolak'")
                kirimNotifikasi(userId, bookingId, "Ruang ${gedung} ${ruangan} gagal dipinjam untuk, ${convertTanggal(tanggal)}, Pukul ${waktu}.", "ditolak")

                Toast.makeText(requireContext(), "Berhasil menolak permintaan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Update Booking Status", "Error updating booking status", e)
                Toast.makeText(requireContext(), "Gagal menolak permintaan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun kirimNotifikasi(userId: String, idBooking: String, pesan: String, status: String) {
        val notifikasiReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("notifikasi").child(userId).child(idBooking)
        val timestamp = System.currentTimeMillis()

        val notifikasi = hashMapOf(
            "idBooking" to idBooking,
            "message" to pesan,
            "status" to status,
            "timestamp" to timestamp

        )

        notifikasiReference.setValue(notifikasi)
            .addOnSuccessListener {
                Log.d("Notifikasi", "Notifikasi berhasil dikirim")
            }
            .addOnFailureListener { e ->
                Log.w("Notifikasi", "Gagal mengirim notifikasi", e)
            }
    }
}
