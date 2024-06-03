package com.app.logistikittp.view.authentication.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.logistikittp.R
import com.app.logistikittp.data.datastore.SharedPref
import com.app.logistikittp.data.model.User
import com.app.logistikittp.databinding.FragmentLoginBinding
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var sharedPref: SharedPref
    private var isPasswordValid = false
    private var isEmailValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref(requireContext())
        databaseRef = FirebaseDatabase.getInstance().getReference("users")

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO
            }

            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.editPassword.addTextChangedListener(textWatcher)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (isPasswordValid) {
                loginUser(email, password)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Kata sandi minimal harus 8 karakter",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSignIn.apply {
            isEnabled = false
            setBackgroundColor(resources.getColor(R.color.Color8))
        }
    }

    private fun checkInputValidity() {
        val email = binding.etEmail.text.toString()
        val password = binding.editPassword.text.toString()

        isEmailValid = email.isNotEmpty()
        isPasswordValid = password.length >= 8

        binding.layoutPassword.error = if (isPasswordValid) null else "Kata sandi minimal harus 8 karakter"

        if (isEmailValid && isPasswordValid) {
            binding.btnSignIn.apply {
                isEnabled = true
                setBackgroundColor(resources.getColor(R.color.enabledTextColor))
            }
        } else {
            binding.btnSignIn.apply {
                isEnabled = true
                setBackgroundColor(resources.getColor(R.color.disabledTextColor))
            }
        }
    }

    private fun saveSession(
        userId: String,
        emailInstitusi: String,
        nama: String,
        posisi: String,
        nimNik: String,
        programStudi: String,
        password: String,
        role: String,
        profilePicture: String
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                sharedPref.saveUserData(
                    userId,
                    emailInstitusi,
                    nama,
                    posisi,
                    nimNik,
                    programStudi,
                    password,
                    role,
                    profilePicture
                )
            } catch (e: Exception) {
                Log.e("LoginFragment", "Gagal menyimpan data user: ${e.message}")
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        databaseRef.orderByChild("emailInstitusi").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user?.password == password) {
                                saveSession(
                                    user.userId.toString(),
                                    user.emailInstitusi.toString(),
                                    user.nama.toString(),
                                    user.posisi.toString(),
                                    user.nimNik.toString(),
                                    user.programStudi.toString(),
                                    user.password.toString(),
                                    user.role.toString(),
                                    user.profilePicture.toString()
                                )
                                Log.d("Role : ", "${user.role}")
                                Toast.makeText(
                                    requireContext(),
                                    "Login berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (user.role == "admin") {
                                    findNavController().navigate(R.id.action_loginFragment_to_homeAdminFragment)
                                } else {
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }

                            } else {
                                showLoginError()
                            }
                            return
                        }
                    } else {
                        showLoginError()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoginError()
                }
            })
    }

    private fun showLoginError() {
        Toast.makeText(
            requireContext(),
            "Email atau Password salah",
            Toast.LENGTH_SHORT
        ).show()
    }
}
