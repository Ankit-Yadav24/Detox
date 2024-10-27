package com.example.detox

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.detox.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener {
            logout()}
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Load current user information
        loadUserProfile()

        // Set up the button click listener to update user profile
        binding.editButton.setOnClickListener {
            updateUserProfile()
        }
        val btn1 = findViewById<ImageView>(R.id.imageView19)
        val btn2 = findViewById<ImageView>(R.id.imageView20)
        val btn3 = findViewById<ImageView>(R.id.imageView21)

        // Set onClick listeners
        btn1.setOnClickListener {
            // Start Activity for the first button
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            // Start Activity for the second button
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        btn3.setOnClickListener {
            // Start Activity for the third button
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }


    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            val userId = it.uid

            // Fetch the user details from Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Set the user details in the UI
                        binding.userName.text = document.getString("name") ?: "User"
                        binding.userPic.let { imageView ->
                            Glide.with(this)
                                .load(document.getString("profilePictureUrl"))
                                .into(imageView)
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserProfile() {
        val newName = binding.newUserName.text.toString()
        val newProfilePicUrl = binding.picUrl.text.toString()
        val newPassword = binding.NewPassword.text.toString()

        val userId = firebaseAuth.currentUser?.uid

        if (newName.isNotEmpty() && newProfilePicUrl.isNotEmpty() && userId != null) {
            // Create a map with updated information
            val updatedUserMap: MutableMap<String, Any> = mutableMapOf(
                "name" to newName,
                "profilePictureUrl" to newProfilePicUrl
            )

            // Update Firestore with new user data
            firestore.collection("users").document(userId).update(updatedUserMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    loadUserProfile()  // Refresh the profile details on successful update
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Update password if provided
            if (newPassword.isNotEmpty()) {
                firebaseAuth.currentUser?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        // Get the current user
        val currentUser = firebaseAuth.currentUser

        // Google sign out and revoke access
        if (currentUser != null) {
            if (currentUser.providerData.any { it.providerId == "google.com" }) {
                // User signed in with Google
                googleSignInClient.revokeAccess().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Firebase sign out
                        firebaseAuth.signOut()
                        redirectToLogin()
                    } else {
                        Toast.makeText(this, "Failed to sign out from Google", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // User signed in with email/password
                firebaseAuth.signOut()
                redirectToLogin()
            }
        }
    }

    private fun redirectToLogin() {
        // Redirect to the login screen after sign-out
        val signOutIntent = Intent(this, Login::class.java)
        signOutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(signOutIntent)
        finish()
    }
}
