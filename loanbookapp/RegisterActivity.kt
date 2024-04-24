package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loanbookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.tvlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnCreateAccount.setOnClickListener {
            val fname = binding.etFirstName.text.toString()
            val lname = binding.etlastName.text.toString()
            val email = binding.etEmailAddress.text.toString()
            val studentid = binding.etStudentID.text.toString()
            val password = binding.etPassword.text.toString()
            val cpassword = binding.etConfirmPassword.text.toString()

            if (fname.isNotEmpty() && lname.isNotEmpty() && email.isNotEmpty() &&studentid.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty()){
            //if (email.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty()){
                if (password == cpassword){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null) {
                                val currentUserId = currentUser.uid

                                val user = User(fname, lname, studentid)

                                database.child(currentUserId).setValue(user).addOnSuccessListener {
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }.addOnFailureListener {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this, "Current user is null", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill in every fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}