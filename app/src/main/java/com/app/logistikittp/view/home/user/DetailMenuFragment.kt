package com.app.logistikittp.view.home.user

import com.app.logistikittp.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.logistikittp.data.model.Ketersediaan
import com.app.logistikittp.data.model.Waktu
import com.app.logistikittp.databinding.FragmentDetailMenuBinding
import com.app.logistikittp.view.home.user.adapter.WaktuAdapter
import com.google.firebase.database.*
import java.util.Calendar

class DetailMenuFragment : Fragment(), WaktuAdapter.WaktuItemClickListener{
    private lateinit var binding: FragmentDetailMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var gedungRef: DatabaseReference
    private var selectedDate: String? = null
    private var ruangan: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gedung = arguments?.getString("gedung")

        binding.tvGedung.text = "Gedung ${gedung!!.capitalize()}"

        database = FirebaseDatabase.getInstance()
        gedungRef = database.getReference("gedung")

        loadGedungData(gedung!!)

        binding.gridWaktuRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.gridWaktuRecyclerView.adapter = WaktuAdapter(emptyList(), this)

        binding.pilihRuangan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRuangan = parent?.getItemAtPosition(position).toString()
                ruangan = selectedRuangan
                Log.d("DetailMenuFragment", "Selected ruangan: $ruangan")
                Log.d("DetailMenuFragment", "Selected gedung: $gedung")
                selectedDate?.let { loadWaktuData(gedung, ruangan!!, it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        selectedDate = "$year-${String.format("%02d", month)}-${String.format("%02d", dayOfMonth)}"

        Log.d("DetailMenuFragment", "Selected date: $selectedDate")

        binding.calendarView.date = calendar.timeInMillis

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val formattedMonth = String.format("%02d", month + 1)
            val formattedDayOfMonth = String.format("%02d", dayOfMonth)

            selectedDate = "$year-$formattedMonth-$formattedDayOfMonth"

            Log.d("DetailMenuFragment", "Selected date: $selectedDate")

            ruangan?.let { loadWaktuData(gedung, it, selectedDate!!) }

            Log.d("DetailMenuFragment", "Selected ruangan: $ruangan")
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnPinjam.setOnClickListener {
            Log.d("DetailMenuFragment", "Gedung: $gedung")
            Log.d("DetailMenuFragment", "Ruangan: ${binding.pilihRuangan.selectedItem}")
            Log.d("DetailMenuFragment", "Tanggal: $selectedDate")
            Log.d("DetailMenuFragment", "Waktu: ${getSelectedWaktu()}")

            val selectedWaktu = getSelectedWaktu()
            if (selectedWaktu.isBlank() || selectedWaktu.split("-").size < 2) {
                Toast.makeText(requireContext(), "Pilih minimal 2 waktu.", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle()
                bundle.putString("gedung", gedung)
                bundle.putString("ruangan", binding.pilihRuangan.selectedItem.toString())
                bundle.putString("tanggal", selectedDate)
                bundle.putString("waktu", selectedWaktu)
                findNavController().navigate(R.id.action_detailMenuFragment_to_inputDataFragment, bundle)
            }
        }
    }



    private fun getWaktuList(): List<String> {
        val waktuList = mutableListOf<String>()
        for (hour in 7..21) {
            waktuList.add(String.format("%02d:00", hour))
        }
        return waktuList
    }

    private fun loadGedungData(gedung: String) {
        val dcGedungRef = gedungRef.child(gedung)

        dcGedungRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listRuangan: MutableList<String> = mutableListOf()
                for (ruanganSnapshot in dataSnapshot.child("ruangan").children) {
                    val namaRuangan = ruanganSnapshot.child("nama").getValue(String::class.java)
                    namaRuangan?.let { listRuangan.add(it) }
                }

                context?.let {
                    val adapter = ArrayAdapter(
                        it,
                        R.layout.spinner_item_layout,
                        listRuangan
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout)
                    binding.pilihRuangan.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun loadWaktuData(gedung: String, ruangan: String, tanggal: String) {
        val waktuRef = gedungRef.child(gedung).child("ruangan").child(ruangan).child("ketersediaan").child(tanggal)

        waktuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val ketersediaan = dataSnapshot.getValue(Ketersediaan::class.java)

                if (ketersediaan != null) {
                    val waktuList = ketersediaan.waktu ?: emptyList()
                    binding.gridWaktuRecyclerView.adapter?.let { adapter ->
                        if (adapter is WaktuAdapter) {
                            adapter.setDataWaktu(waktuList)
                        }
                    }
                } else {
                    val waktuList = getWaktuList()
                    val waktuAvailabilityList = mutableListOf<Waktu>()
                    waktuList.forEach { jam ->
                        waktuAvailabilityList.add(Waktu(jam, true))
                    }
                    val newKetersediaan = Ketersediaan(tanggal, waktuAvailabilityList)
                    waktuRef.setValue(newKetersediaan)
                        .addOnSuccessListener {
                            binding.gridWaktuRecyclerView.adapter?.let { adapter ->
                                if (adapter is WaktuAdapter) {
                                    adapter.setDataWaktu(waktuAvailabilityList)
                                }
                            }
                            Log.d("loadWaktuData", "Waktu telah ditambahkan.")
//                            Toast.makeText(context, "Waktu telah ditambahkan.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("loadWaktuData", "Error adding waktu: $e")
//                            Toast.makeText(context, "Gagal menambahkan waktu.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("loadWaktuData", "Error: $databaseError")
            }
        })
    }

    override fun onWaktuClicked(waktu: String) {
        Log.d("DetailMenuFragment", "Selected waktu: $waktu")
    }

    private fun getSelectedWaktu(): String {
        val adapter = binding.gridWaktuRecyclerView.adapter as? WaktuAdapter
        return adapter?.getSelectedWaktu() ?: ""
    }

}
