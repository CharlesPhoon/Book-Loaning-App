package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loanbookapp.databinding.ActivityLoanListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoanListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoanListBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var database: FirebaseDatabase
    private lateinit var loansReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        loansReference = database.reference.child("Loans")
        auth = FirebaseAuth.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.loanRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val loanAdapter = LoanAdapter()
        recyclerView.adapter = loanAdapter

        val currentUserId = auth.currentUser?.uid
        val query = loansReference.orderByChild("userId").equalTo(currentUserId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loanList = mutableListOf<Loan>()

                for (loanSnapshot in snapshot.children) {
                    val bookName = loanSnapshot.child("bookName").getValue(String::class.java)
                    val author = loanSnapshot.child("author").getValue(String::class.java)
                    val loanDateTime = loanSnapshot.child("loanDateTime").getValue(String::class.java)
                    val loanPeriodDaysLong = loanSnapshot.child("loanPeriodDays").getValue(Long::class.java)
                    val loanPeriodDays = loanPeriodDaysLong?.toString() ?: ""
                    val studentId = loanSnapshot.child("studentId").getValue(String::class.java)
                    val userName = loanSnapshot.child("userName").getValue(String::class.java)

                    val loan = Loan(bookName, author, loanDateTime, loanPeriodDays, studentId, userName)
                    loanList.add(loan)
                }

                // Now you can use loanList to populate your UI or adapter
                // For example, you might want to display it in a RecyclerView
                // adapter.submitList(loanList)
                loanAdapter.submitList(loanList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        })

        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_dashboard -> {
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
}