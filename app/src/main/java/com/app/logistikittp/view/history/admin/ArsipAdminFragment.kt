package com.app.logistikittp.view.history.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentArsipAdminBinding
import com.app.logistikittp.view.history.admin.adapter.ArsipAdminAdapter
import com.google.firebase.database.*
import java.util.Locale

class ArsipAdminFragment : Fragment() {
    lateinit var binding: FragmentArsipAdminBinding
    private lateinit var historiAdapter: ArsipAdminAdapter
    private val bookingList = mutableListOf<Booking>()
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("booking")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArsipAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historiAdapter = ArsipAdminAdapter(requireContext(), bookingList)
        binding.rvPeminjaman.adapter = historiAdapter
        binding.rvPeminjaman.layoutManager = LinearLayoutManager(requireContext())

        loadBookingHistory()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchbox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSearch(newText.orEmpty())
                return true
            }
        })

        binding.btnSortAndFilter.setOnClickListener {
            showSortAndFilterDialog()
        }
    }

    private fun loadBookingHistory() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHistoriList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let { newHistoriList.add(it) }
                    }
                }
                historiAdapter.updateData(newHistoriList)

                for (booking in newHistoriList) {
                    booking.asal_peminjam?.let { println("Asal Peminjam: $it") }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        })
    }

    private fun filterSearch(query: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            if (it.asal_peminjam?.contains(query, ignoreCase = true) == true) {
                                filteredList.add(it)
                            }
                        }
                    }
                }
                historiAdapter.updateData(filteredList)
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        })
    }

    private fun showSortAndFilterDialog() {
        val options = arrayOf("A-Z", "Z-A", "Gedung", "Ruang", "Status Pesanan", "Hapus Filter")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sort & Filter")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    sortByAscending()
                    dialog.dismiss()
                }
                1 -> {
                    sortByDescending()
                    dialog.dismiss()
                }
                2 -> {
                    filterByGedung()
                    dialog.dismiss()
                }
                3 -> {
                    fetchRooms()
                    dialog.dismiss()
                }
                4 -> {
                    showStatusOptionsDialog()
                    dialog.dismiss()
                }
                5 -> {
                    resetFilter()
                    dialog.dismiss()
                }
            }
        }
        builder.setNegativeButton("Kembali") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun sortByAscending() {
        bookingList.sortBy { it.asal_peminjam }
        historiAdapter.notifyDataSetChanged()
    }

    private fun sortByDescending() {
        bookingList.sortByDescending { it.asal_peminjam }
        historiAdapter.notifyDataSetChanged()
    }

    private fun filterByGedung() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("gedung")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val buildingOptions = snapshot.children.mapNotNull { it.key }.toTypedArray()
                showBuildingOptionsDialog(buildingOptions)
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO
            }
        })
    }

    private fun fetchRooms() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("gedung")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomOptions = mutableListOf<String>()
                for (buildingSnapshot in snapshot.children) {
                    for (roomSnapshot in buildingSnapshot.child("ruangan").children) {
                        roomOptions.add(roomSnapshot.key.toString())
                    }
                }
                showRoomOptionsDialog(roomOptions.toTypedArray())
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO
            }
        })
    }

    private fun showRoomOptionsDialog(roomOptions: Array<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Ruang")
        builder.setItems(roomOptions) { dialog, which ->
            val selectedRoom = roomOptions[which]
            filterByRuang(selectedRoom)
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun filterByRuang(selectedRoom: String? = null) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHistoriList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            if (selectedRoom == null || it.ruangan.equals(selectedRoom, ignoreCase = true)) {
                                newHistoriList.add(it)
                            }
                        }
                    }
                }
                historiAdapter.updateData(newHistoriList)
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO
            }
        })
    }

    private fun showBuildingOptionsDialog(buildingOptions: Array<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Gedung")
        builder.setItems(buildingOptions) { dialog, which ->
            val selectedBuilding = buildingOptions[which]
            filterByGedung(selectedBuilding)
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun filterByGedung(selectedBuilding: String? = null) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHistoriList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            if (selectedBuilding == null || it.gedung.equals(selectedBuilding, ignoreCase = true)) {
                                newHistoriList.add(it)
                            }
                        }
                    }
                }
                historiAdapter.updateData(newHistoriList)
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO
            }
        })
    }

    private fun filterByStatus(status: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHistoriList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let {
                            if (it.status.equals(status, ignoreCase = true)) {
                                newHistoriList.add(it)
                            }
                        }
                    }
                }
                historiAdapter.updateData(newHistoriList)
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO
            }
        })
    }

    private fun showStatusOptionsDialog() {
        val statusOptions = arrayOf("ditolak", "diterima")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Status Pesanan")
        builder.setItems(statusOptions) { dialog, which ->
            val selectedStatus = statusOptions[which].toLowerCase(Locale.getDefault())
            filterByStatus(selectedStatus)
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun resetFilter() {
        loadBookingHistory()
    }
}
