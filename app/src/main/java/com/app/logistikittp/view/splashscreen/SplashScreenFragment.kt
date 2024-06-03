package com.app.logistikittp.view.splashscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.Navigation
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment() {
    lateinit var binding : FragmentSplashScreenBinding
    private lateinit var sharedPref: SharedPref
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        startSplashScreen()
    }

    private fun startSplashScreen() {
        sharedPref.getUserId.asLiveData().observe(viewLifecycleOwner) { userId ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (userId.equals("Undefined")) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_splashScreenFragment_to_loginFragment)
                } else {
                    sharedPref.getRole.asLiveData().observe(viewLifecycleOwner) { role ->
                        if (role.equals("admin")) {
                            Navigation.findNavController(requireView()).navigate(R.id.action_splashScreenFragment_to_homeAdminFragment)
                        } else {
                            Navigation.findNavController(requireView()).navigate(R.id.action_splashScreenFragment_to_homeFragment)
                        }
                    }
                }
            }, 1000)
        }
    }


}