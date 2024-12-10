package com.example.gymmembershipapp.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.bumptech.glide.Glide
import com.example.gymmembershipapp.ui.activity.LoginActivity
import com.example.gymmembershipapp.R
import com.example.gymmembershipapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {


    private lateinit var binding:FragmentProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        val userId = firebaseAuth.currentUser?.uid ?: return

        // Load and prefill user data
        loadUserProfile(userId)

        // Handle image selection
        binding.camera.setOnClickListener {
            Toast.makeText(requireContext(), "Firebase Storage billing Problem", Toast.LENGTH_SHORT).show()
            //selectImage()
        }

        binding.logOut.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.changePassword.setOnClickListener {
            handleChangePassword()
        }

        // Handle profile update
        binding.updateProfile.setOnClickListener {
            val fullName = binding.name.text.toString().trim()
            val contactNumber = binding.number.text.toString().trim()

            if (fullName.isEmpty() || contactNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(userId, fullName, contactNumber)
        }
    }

    private fun handleChangePassword() {

        val progress=ProgressDialog(requireContext())
        progress.setTitle("Password Updating...")
        progress.setCancelable(false)
        progress.show()

        val currentPassword = binding.currentPassword.text.toString().trim()
        val newPassword = binding.newPassword.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPassword.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required.", Toast.LENGTH_SHORT).show()
            progress.dismiss()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(requireContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show()
            progress.dismiss()
            return
        }

        val user = firebaseAuth.currentUser
        val email = user?.email ?: return


        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Update the password
                progress.dismiss()
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Toast.makeText(requireContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show()

                        // Log out the user after password change
                        firebaseAuth.signOut()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Failed to change password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                progress.dismiss()
                Toast.makeText(requireContext(), "Authentication failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadUserProfile(userId: String) {
        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            val fullName = snapshot.child("fullName").value?.toString() ?: ""
            val contactNumber = snapshot.child("contactNumber").value?.toString() ?: ""
            val profileImageUrl = snapshot.child("profileImage").value?.toString() ?: ""

            // Prefill fields
            binding.name.setText(fullName)
            binding.number.setText(contactNumber)


            if (profileImageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.circle)
                    .into(binding.profile)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data
            binding.profile.setImageURI(imageUri)
        }
    }

    private fun updateProfile(userId: String, fullName: String, contactNumber: String) {
        if (imageUri != null) {
            val storageRef = storage.reference.child("profile_images/$userId.jpg")
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProfile(userId, fullName, contactNumber, uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
        } else {
            saveProfile(userId, fullName, contactNumber, null)
        }
    }

    private fun saveProfile(userId: String, fullName: String, contactNumber: String, profileImageUrl: String?) {
        val updates = mapOf(
            "fullName" to fullName,
            "contactNumber" to contactNumber,
            "profileImage" to profileImageUrl
        )

        database.child("users").child(userId).updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun showLogoutConfirmationDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _, _ ->
            logOutUser() // Call the logout function
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Close the dialog
        }
        builder.create().show()
    }

    private fun logOutUser() {
         //Clear JWT Token from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("JWT_TOKEN") // Remove the token
        editor.apply()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
