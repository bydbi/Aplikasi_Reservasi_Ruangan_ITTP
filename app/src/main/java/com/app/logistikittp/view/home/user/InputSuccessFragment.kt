package com.app.logistikittp.view.home.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.databinding.FragmentInputSuccessBinding

class InputSuccessFragment : Fragment() {
    lateinit var binding : FragmentInputSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnKembali.setOnClickListener{
            findNavController().navigate(R.id.action_inputSuccessFragment_to_homeFragment)
        }
    }
}