package com.app.logistikittp.view.home.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.DialogFragment
import com.app.logistikittp.R
import com.app.logistikittp.databinding.CustomConfirmationDialogBinding

class ConfirmationDialogFragment(private val message: String,
                                 private val onConfirm: () -> Unit) : DialogFragment() {

    lateinit var binding : CustomConfirmationDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = CustomConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnYes.setOnClickListener {
            onConfirm()
            navigateToHistory()
            dismiss()
        }

        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun navigateToHistory() {
        findNavController().navigate(R.id.action_prosesPeminjamanFragment_to_historiAdminFragment)
    }

}