package com.app.logistikittp.view.authentication.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.data.model.User
import com.app.logistikittp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {
    lateinit var binding : FragmentRegisterBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordValid = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        val passwordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                isPasswordValid = password.length >= 8
                if (isPasswordValid) {
                    binding.layoutPassword.error = null
                    if (password == binding.editPasswordKonfirmasi.text.toString()) {
                        binding.layoutPasswordKonfirmasi.error = null
                    }
                } else {
                    binding.layoutPassword.error = "Kata sandi minimal harus 8 karakter"
                }
                checkInputValidity()
            }
        }

        val confirmPasswordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO
            }

            override fun afterTextChanged(s: Editable?) {
                val confirmPassword = s.toString()
                if (!isPasswordValid) {
                    binding.layoutPasswordKonfirmasi.error = null
                    return
                }
                val password = binding.editPassword.text.toString()
                if (confirmPassword.length >= 8 && password == confirmPassword) {
                    binding.layoutPasswordKonfirmasi.error = null
                } else {
                    binding.layoutPasswordKonfirmasi.error = "Kata sandi minimal harus 8 karakter dan cocok"
                }
                checkInputValidity()
            }
        }
        binding.editEmail.addTextChangedListener(confirmPasswordWatcher)
        binding.editNama.addTextChangedListener(confirmPasswordWatcher)
        binding.editPosisi.addTextChangedListener(confirmPasswordWatcher)
        binding.editNimNik.addTextChangedListener(confirmPasswordWatcher)
        binding.editProgramStudi.addTextChangedListener(confirmPasswordWatcher)

        binding.editPassword.addTextChangedListener(passwordWatcher)
        binding.editPasswordKonfirmasi.addTextChangedListener(confirmPasswordWatcher)

        binding.btnRegister.setOnClickListener{
            val emailInstitusi = binding.editEmail.text.toString()
            val nama = binding.editNama.text.toString()
            val posisi = binding.editPosisi.text.toString()
            val nimNik = binding.editNimNik.text.toString()
            val programStudi = binding.editProgramStudi.text.toString()
            val password = binding.editPassword.text.toString()
            val confirmPassword = binding.editPasswordKonfirmasi.text.toString()

            if (validateInput(emailInstitusi,nama,posisi,nimNik,programStudi,password,confirmPassword)) {
                signUp(emailInstitusi, password)
            }
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnRegister.apply {
            isEnabled = false
            setBackgroundColor(resources.getColor(R.color.disabledTextColor))
        }

    }

    private fun checkInputValidity() {
        val emailInstitusi = binding.editEmail.text.toString()
        val nama = binding.editNama.text.toString()
        val posisi = binding.editPosisi.text.toString()
        val nimNik = binding.editNimNik.text.toString()
        val programStudi = binding.editProgramStudi.text.toString()
        val password = binding.editPassword.text.toString()
        val confirmPassword = binding.editPasswordKonfirmasi.text.toString()

        val isEmailValid = emailInstitusi.isNotEmpty()
        val isNamaValid = nama.isNotEmpty()
        val isPosisiValid = posisi.isNotEmpty()
        val isNimNikValid = nimNik.isNotEmpty()
        val isProgramStudiValid = programStudi.isNotEmpty()

        if (isEmailValid && isNamaValid && isPosisiValid && isNimNikValid && isProgramStudiValid && isPasswordValid && password == confirmPassword) {
            binding.btnRegister.apply {
                isEnabled = true
                setBackgroundColor(resources.getColor(R.color.enabledTextColor))
            }
        } else {
            binding.btnRegister.apply {
                isEnabled = false
                setBackgroundColor(resources.getColor(R.color.disabledTextColor))
            }
        }
    }

    private fun validateInput(
        email: String,
        nama: String,
        posisi: String,
        nimNik: String,
        programStudi: String,
        password: String,
        konfirmasiPassword: String): Boolean {
        if (email.isEmpty() || nama.isEmpty() || posisi.isEmpty() || nimNik.toString().isEmpty() || programStudi.isEmpty() || password.isEmpty() || konfirmasiPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != konfirmasiPassword) {
            Toast.makeText(requireContext(), "Password tidak sama", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    saveUserData(user.uid)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Registrasi gagal: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveUserData(userId: String) {
        val emailInstitusi = binding.editEmail.text.toString()
        val nama = binding.editNama.text.toString()
        val posisi = binding.editPosisi.text.toString()
        val nimNik = binding.editNimNik.text.toString()
        val programStudi = binding.editProgramStudi.text.toString()
        val password = binding.editPassword.text.toString()
        val profilePicture = "https://firebasestorage.googleapis.com/v0/b/ittp-logistik.appspot.com/o/profile.png?alt=media&token=a9b10d3d-9abb-494a-a41f-6cfa7c8c92ed"

        val user = User(
            userId,
            emailInstitusi,
            nama,
            posisi,
            nimNik,
            programStudi,
            password,
            "user",
            profilePicture
        )

        databaseRef.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Berhasil mendaftar", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Gagal menyimpan data pengguna: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}