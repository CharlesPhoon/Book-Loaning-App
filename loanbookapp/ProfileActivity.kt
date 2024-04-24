package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loanbookapp.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private val auth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editTextFirstName = binding.editTextFirstName
        val editTextLastName = binding.editTextLastName
        val editTextPassword = binding.editTextPassword
        val buttonSaveChanges = binding.buttonSaveChanges
        val buttonLogout = binding.buttonLogout

        buttonSaveChanges.setOnClickListener {
            val newFirstName = editTextFirstName.text.toString()
            val newLastName = editTextLastName.text.toString()
            val newPassword = editTextPassword.text.toString()

            // Check if any of the fields is empty
            if (newFirstName.isEmpty() || newLastName.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = FirebaseAuth.getInstance().currentUser
            if (newPassword.isNotEmpty()) {
                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Password updated successfully
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            // Failed to update password
                            Toast.makeText(this, "Error updating password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            val userId = user?.uid
            if (userId != null) {
                val userRef = databaseReference.child("Users").child(userId)
                userRef.child("firstName").setValue(newFirstName)
                userRef.child("lastName").setValue(newLastName)
                    .addOnSuccessListener {
                        // Handle success
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        buttonLogout.setOnClickListener {
            // Log out the user
            auth.signOut()

            // Redirect to the login screen or any other desired screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finish the current activity to prevent the user from navigating back
            finish()
        }

        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, LoanListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }
}