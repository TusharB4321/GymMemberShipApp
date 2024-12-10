package com.example.gymmembershipapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gymmembershipapp.databinding.ActivityLoginBinding
import com.example.gymmembershipapp.repository.UserRepository
import com.example.gymmembershipapp.utils.Constant
import com.example.gymmembershipapp.utils.Resources
import com.example.gymmembershipapp.viewmodel.UserViewModel
import com.example.gymmembershipapp.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val database=FirebaseDatabase.getInstance()
        val auth=FirebaseAuth.getInstance()
        val userRepository = UserRepository(firebaseAuth = auth, database = database)
        val viewModelFactory = ViewModelFactory(userRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        // Observe login status
        observeLoginStatus()

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLoginApply.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                loginViewModel.loginUser(email, password)
            }
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.status.observe(this) { resource ->
            when (resource) {
                is Resources.Loading -> {
                    Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                }
                is Resources.Success -> {
                    resource.data?.let { token ->
                        saveToken(token)
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToMainActivity()
                    }
                }
                is Resources.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (!Constant.isEmailValid(email) { showValidationError(it.message) }) {
            isValid = false
        }

        if (!Constant.isPasswordValid(password) { showValidationError(it.message) }) {
            isValid = false
        }

        return isValid
    }

    private fun showValidationError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
