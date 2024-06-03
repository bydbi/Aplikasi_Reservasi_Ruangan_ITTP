package com.app.logistikittp.view.notif

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.Notifikasi
import com.app.logistikittp.databinding.FragmentNotifBinding
import com.app.logistikittp.view.notif.adapter.NotifAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class NotifFragment : Fragment() {
    lateinit var binding : FragmentNotifBinding
    private lateinit var sharedPref: SharedPref
    private lateinit var notifikasiAdapter: NotifAdapter
    private val notifikasiList = mutableListOf<Notifikasi>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotifBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())

        notifikasiAdapter = NotifAdapter(requireContext(), notifikasiList)
        binding.rvNotif.adapter = notifikasiAdapter
        binding.rvNotif.layoutManager = LinearLayoutManager(requireContext())

        loadNotifications()

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            sharedPref.getUserId.collect { userId ->
                Log.d("NotifFragment", "User ID: $userId")
                val databaseReference: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("notifikasi").child(userId)

                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newNotifikasiList = mutableListOf<Notifikasi>()
                        for (notifSnapshot in snapshot.children) {
                            val notifikasi = notifSnapshot.getValue(Notifikasi::class.java)
                            notifikasi?.let { newNotifikasiList.add(it) }
                        }
                        newNotifikasiList.sortByDescending { it.timestamp }
                        notifikasiAdapter.updateData(newNotifikasiList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO: Handle error
                    }
                })
            }
        }
    }

}