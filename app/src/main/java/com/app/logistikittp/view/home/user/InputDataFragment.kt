package com.app.logistikittp.view.home.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentInputDataBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InputDataFragment : Fragment() {
    private lateinit var binding: FragmentInputDataBinding
    private lateinit var sharedPref: SharedPref
    private val REQUEST_PICK_PDF = 101
    private var suratIzinUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())

        binding.btnKonfirmasi.apply {
            val disabledColor = ContextCompat.getColor(requireContext(), R.color.disabledTextColor)
            val colorStateList = ColorStateList.valueOf(disabledColor)
            backgroundTintList = colorStateList
        }
        val gedung = arguments?.getString("gedung")
        val ruangan = arguments?.getString("ruangan")
        val tanggal = arguments?.getString("tanggal")
        val waktu = arguments?.getString("waktu")

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSuratIzin.setOnClickListener {
            checkAndRequestPermission()
        }

        binding.btnKonfirmasi.setOnClickListener {
            val peminjam = binding.editPeminjam.text.toString()
            val asalPeminjam = binding.editAsalPeminjam.text.toString()
            val nimNik = binding.editNimNik.text.toString()
            val noHp = binding.editNomorHp.text.toString()
            val keterangan = binding.editKeteranganAcara.text.toString()
            if (gedung != null && ruangan != null && tanggal != null && waktu != null) {
                uploadPdfToFirebaseStorage(gedung, ruangan, tanggal, waktu, peminjam, asalPeminjam, nimNik, noHp, keterangan, suratIzinUri)
            } else {
                Toast.makeText(requireContext(), "Data tidak lengkap", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editPeminjam.addTextChangedListener(textWatcher)
        binding.editAsalPeminjam.addTextChangedListener(textWatcher)
        binding.editNimNik.addTextChangedListener(textWatcher)
        binding.editNomorHp.addTextChangedListener(textWatcher)
        binding.editKeteranganAcara.addTextChangedListener(textWatcher)

        updateConfirmButtonState()
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openPdfPicker()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_PICK_PDF)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openPdfPicker()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Cannot select PDF.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_PDF && resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            suratIzinUri = data?.data
            binding.tvSuratIzinPath.text = suratIzinUri?.path ?: "No file selected"
            updateConfirmButtonState()
        }
    }

    private fun uploadPdfToFirebaseStorage(gedung: String, ruangan: String, tanggal: String, waktu: String, peminjam: String, asalPeminjam: String, nimNik: String, noHp: String, keterangan: String, pdfUri: Uri?) {
        pdfUri?.let { uri ->
            lifecycleScope.launch {
                sharedPref.getUserId.firstOrNull()?.let { userId ->
                    val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val storageRef = FirebaseStorage.getInstance().reference
                        .child("surat_izin")
                        .child(userId)
                        .child(currentDate)
                        .child(uri.lastPathSegment ?: "")

                    val uploadTask = storageRef.putFile(uri)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result
                            updateBookingWithPdfUrl(gedung, ruangan, tanggal, waktu, peminjam, asalPeminjam, nimNik, noHp, keterangan, downloadUri.toString())
                        } else {
                            Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "No PDF file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBookingWithPdfUrl(gedung: String, ruangan: String, tanggal: String, waktu: String, peminjam: String, asalPeminjam: String, nimNik: String, noHp: String, keterangan: String, pdfUrl: String) {
        val bookingRef = FirebaseDatabase.getInstance().getReference("booking")

        lifecycleScope.launch {
            sharedPref.getUserId.firstOrNull()?.let { userId ->
                Log.d("InputDataFragment", "User ID: $userId")
                val newBookingRef = bookingRef.child(userId).push()
                val bookingId = newBookingRef.key

                val booking = Booking(
                    idBooking = bookingId,
                    userId = userId,
                    gedung = gedung,
                    ruangan = ruangan,
                    tanggal_pinjam = tanggal,
                    waktu_pinjam = waktu,
                    status = "pending",
                    peminjam = peminjam,
                    asal_peminjam = asalPeminjam,
                    nim_nik = nimNik,
                    nomor_hp = noHp,
                    keterangan_acara = keterangan,
                    surat_izin = pdfUrl
                )

                newBookingRef.setValue(booking)
                    .addOnSuccessListener {
                        Log.d("InputDataFragment", "Booking success")
                        findNavController().navigate(R.id.action_inputDataFragment_to_inputSuccessFragment)
                    }
                    .addOnFailureListener { e ->
                        Log.e("InputDataFragment", "Booking failed", e)
                    }
            } ?: run {
                Log.e("InputDataFragment", "User ID is null or empty")
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateConfirmButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateConfirmButtonState() {
        val isFormFilled = binding.editPeminjam.text!!.isNotEmpty() &&
                binding.editAsalPeminjam.text!!.isNotEmpty() &&
                binding.editNimNik.text!!.isNotEmpty() &&
                binding.editNomorHp.text!!.isNotEmpty() &&
                binding.editKeteranganAcara.text!!.isNotEmpty() &&
                suratIzinUri != null

        binding.btnKonfirmasi.isEnabled = isFormFilled
        val color = if (isFormFilled) {
            ContextCompat.getColor(requireContext(), R.color.enabledTextColor)
        } else {
            ContextCompat.getColor(requireContext(), R.color.disabledTextColor)
        }
        val colorStateList = ColorStateList.valueOf(color)
        binding.btnKonfirmasi.backgroundTintList = colorStateList

    }
}
