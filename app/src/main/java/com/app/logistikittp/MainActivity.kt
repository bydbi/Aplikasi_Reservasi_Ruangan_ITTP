package com.app.logistikittp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.fragmentContainerView)
        if (navController.currentDestination?.id == R.id.homeFragment || navController.currentDestination?.id == R.id.loginFragment || navController.currentDestination?.id == R.id.inputSuccessFragment || navController.currentDestination?.id == R.id.homeAdminFragment) {
            // Do nothing
            // Supaya tidak bisa back
        } else {
            super.onBackPressed()
        }
    }
}