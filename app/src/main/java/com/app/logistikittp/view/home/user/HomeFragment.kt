package com.app.logistikittp.view.home.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.btnGedungDc.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("gedung", "dc")
            findNavController().navigate(R.id.action_homeFragment_to_detailMenuFragment, bundle)
        }

        binding.btnGedungIot.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("gedung", "iot")
            findNavController().navigate(R.id.action_homeFragment_to_detailMenuFragment, bundle)
        }

        binding.btnGedungTt.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("gedung", "tt")
            findNavController().navigate(R.id.action_homeFragment_to_detailMenuFragment, bundle)
        }

        binding.btnGedungRektorat.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("gedung", "rektorat")
            findNavController().navigate(R.id.action_homeFragment_to_detailMenuFragment, bundle)
        }

        binding.btnGedungDsp.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("gedung", "dsp")
            findNavController().navigate(R.id.action_homeFragment_to_detailMenuFragment, bundle)
        }

        binding.btnOrderHistory.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_historiFragment)
        }

        binding.bellButton.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_notifFragment)
        }
    }
}