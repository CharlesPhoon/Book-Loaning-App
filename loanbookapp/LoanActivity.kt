package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loanbookapp.databinding.ActivityLoanBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LoanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoanBinding // Make sure to replace with your actual binding class
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var usersDatabase: DatabaseReference
    private lateinit var loansDatabase: DatabaseReference
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        usersDatabase = FirebaseDatabase.getInstance().getReference("Users")
        loansDatabase = FirebaseDatabase.getInstance().getReference("Loans")


        val imageUrl = intent.getStringExtra("imgUrl")
        val bookName = intent.getStringExtra("name")
        val author = intent.getStringExtra("author")

        Glide.with(this)
            .load(imageUrl)
            .into(binding.loanImageView)

        binding.loanNameTextView.text = bookName
        binding.loanAuthorTextView.text = author

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid

            usersDatabase.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val firstName = snapshot.child("firstName").getValue(String::class.java)
                        val lastName = snapshot.child("lastName").getValue(String::class.java)
                        val fullName = "$firstName $lastName"
                        val studentId = snapshot.child("studentId").getValue(String::class.java)

                        // Set the user data in the appropriate TextViews
                        binding.nameTxt2.text = fullName
                        binding.studentIdTxt2.text = studentId
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(applicationContext, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.confirmLoanButton.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val currentUserId = currentUser.uid
                val userName = binding.nameTxt2.text.toString()
                val studentId = binding.studentIdTxt2.text.toString()

                // Retrieve additional information
                val bookName = intent.getStringExtra("name")
                val author = intent.getStringExtra("author")
                val loanPeriod = binding.loanPeriodEt.text.toString()
                if (loanPeriod.isEmpty()) {
                    // Display an error message indicating that the loan period is required
                    binding.loanPeriodEt.error = "Loan period is required"
                } else {
                    val loanPeriodDays = loanPeriod.toInt()
                    val currentDateAndTime = getCurrentDateTime()
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentDateAndTime)

                    // Generate a new child node reference under "Loans"
                    val loanReference = loansDatabase.push()
                    // Get the unique key generated by push()
                    val loanId = loanReference.key

                    if (loanId != null) {
                        val loanData = Loan(
                            userId = currentUserId,
                            userName = userName,
                            studentId = studentId,
                            bookName = bookName,
                            author = author,
                            loanDateTime = formattedDate,
                            loanPeriodDays = loanPeriodDays
                        )
                        loanReference.setValue(loanData)
                            .addOnSuccessListener {
                                Toast.makeText(applicationContext, "Loan confirmed and saved to database", Toast.LENGTH_SHORT).show()
                                // Redirect to MainActivity
                                val mainActivityIntent = Intent(this@LoanActivity, MainActivity::class.java)
                                startActivity(mainActivityIntent)

                                // Finish the current activity to prevent going back to LoanActivity when pressing the back button
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(applicationContext, "Failed to confirm loan", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Handle the case where the generated key is null
                        Toast.makeText(applicationContext, "Failed to generate a unique key for the loan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}