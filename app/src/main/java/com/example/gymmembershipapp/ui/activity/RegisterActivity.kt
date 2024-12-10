package com.example.gymmembershipapp.ui.activity

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gymmembershipapp.databinding.ActivityRegisterBinding
import com.example.gymmembershipapp.repository.UserRepository
import com.example.gymmembershipapp.utils.Constant
import com.example.gymmembershipapp.utils.Resources
import com.example.gymmembershipapp.viewmodel.UserViewModel
import com.example.gymmembershipapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val userRepository = UserRepository(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance())
        userViewModel = ViewModelProvider(this, ViewModelFactory(userRepository))[UserViewModel::class.java]

        val progress = ProgressDialog(this)
        progress.setTitle("Please Wait...")
        progress.setCancelable(false)

        allReadyRegister()
        val membershipOptions = arrayOf("Basic", "Premium")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, membershipOptions)
        binding.spinnerMembership.adapter = adapter

        binding.tvLogIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnSignupApply.setOnClickListener {
            progress.show()
            val fullName = binding.etSignupName.text.toString()
            val email = binding.etSignupEmail.text.toString()
            val password = binding.etSignupPassword.text.toString()
            val contactNumber = binding.etSignupNumber.text.toString()
            val membership = binding.spinnerMembership.selectedItem.toString()

            validateAndRegisterUser(fullName, email, password, contactNumber, membership, progress)
        }

        // Observe ViewModel status
        userViewModel.status.observe(this) { resource ->
            when (resource) {
                is Resources.Loading -> progress.show()
                is Resources.Success -> {
                    progress.dismiss()
                    saveToken(resource.data)
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is Resources.Error -> {
                    progress.dismiss()
                    Toast.makeText(this, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateAndRegisterUser(
        fullName: String,
        email: String,
        password: String,
        contactNumber: String,
        membership: String,
        progress: ProgressDialog
    ) {
        if (Constant.isNameValid(fullName) { showError(it.message) } &&
            Constant.isEmailValid(email) { showError(it.message) } &&
            Constant.isPasswordValid(password) { showError(it.message) } &&
            Constant.isNumberValid(contactNumber) { showError(it.message) }
        ) {
            val userDetails = mapOf(
                "fullName" to fullName,
                "email" to email,
                "password" to password,
                "contactNumber" to contactNumber,
                "membershipType" to membership
            )

            userViewModel.registerUser(email, password, userDetails)
        } else {
            progress.dismiss()
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToken(token: String?) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    private fun allReadyRegister() {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val token = sharedPreferences.getString("JWT_TOKEN", null)
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
