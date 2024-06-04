package com.app.logistikittp.view.history.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Booking
import com.app.logistikittp.databinding.FragmentHistoryBinding
import com.app.logistikittp.view.history.user.adapter.HistoryAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sharedPref: SharedPref
    private val historiList = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())

        historyAdapter = HistoryAdapter(requireContext(), historiList)
        binding.rvRiwayat.adapter = historyAdapter
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())

        loadBookingHistory()

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun loadBookingHistory() {
        lifecycleScope.launch {
            sharedPref.getUserId.collect { userId ->
                Log.d("InputDataFragment", "User ID: $userId")
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("booking").child(userId)

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newHistoriList = mutableListOf<Booking>()
                        for (bookingSnapshot in snapshot.children) {
                            val booking = bookingSnapshot.getValue(Booking::class.java)
                            booking?.let { newHistoriList.add(it) }
                        }
                        historyAdapter.updateData(newHistoriList)

                        if (newHistoriList.isEmpty()){
                            binding.tvNoHistory.visibility = View.VISIBLE
                        } else{
                            binding.tvNoHistory.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO
                    }
                })
            }
        }
    }
}
