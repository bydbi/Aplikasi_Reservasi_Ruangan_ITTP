package com.app.logistikittp.view.home.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentHomeAdminBinding
import com.app.logistikittp.view.home.admin.adapter.HomeAdminAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeAdminFragment : Fragment() {
    lateinit var binding : FragmentHomeAdminBinding
    private lateinit var homeAdapter: HomeAdminAdapter
    private lateinit var sharedPref: SharedPref
    private val bookingList = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())

        homeAdapter = HomeAdminAdapter(requireContext(), bookingList)
        binding.rvPeminjaman.adapter = homeAdapter
        binding.rvPeminjaman.layoutManager = LinearLayoutManager(requireContext())

        loadBookingHistory()

        binding.btnPeminjamanHistori.setOnClickListener{
            findNavController().navigate(R.id.action_homeAdminFragment_to_historiAdminFragment)
        }

        binding.btnLogoutAdmin.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO) {
                sharedPref.removeSession()
            }
            findNavController().navigate(R.id.action_homeAdminFragment_to_loginFragment)
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBookingHistory() {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("booking")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newHistoriList = mutableListOf<Booking>()
                for (userSnapshot in snapshot.children) {
                    for (bookingSnapshot in userSnapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        booking?.let { newHistoriList.add(it) }
                    }
                }
                homeAdapter.updateData(newHistoriList)
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO
            }
        })
    }
}