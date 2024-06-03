package com.app.logistikittp.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding
    lateinit var sharedPref: SharedPref
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseRef = FirebaseDatabase.getInstance().getReference("users")
        sharedPref = SharedPref(requireContext())

        lifecycleScope.launch {
            sharedPref.getUserId.collect { id ->
                binding.tvName.text = sharedPref.getNama.firstOrNull() ?: "Undefined"
                binding.tvPosisi.text = sharedPref.getPosisi.firstOrNull() ?: "Undefined"
                binding.tvEmail.text = sharedPref.getEmailInstitusi.firstOrNull() ?: "Undefined"
                binding.tvNim.text = sharedPref.getNimNik.firstOrNull() ?: "Undefined"
                binding.tvProdi.text = sharedPref.getProgramStudi.firstOrNull() ?: "Undefined"
                Glide.with(requireContext())
                    .load(sharedPref.getProfilePicture.firstOrNull() ?: "Undefined")
                    .into(binding.circleImageView)
            }
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnLogout.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO) {
                sharedPref.removeSession()
            }
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
        }
    }

}