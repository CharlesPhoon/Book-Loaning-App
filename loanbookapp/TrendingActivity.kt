package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loanbookapp.databinding.ActivityTrendingBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class TrendingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrendingBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val imageUrl = intent.getStringExtra("imgUrl")
        val bookName = intent.getStringExtra("name")
        val author = intent.getStringExtra("author")
        val description = intent.getStringExtra("description")

        // Load and display data in the DetailActivity
        Glide.with(this)
            .load(imageUrl)
            .into(binding.detailImageView)

        binding.detailNameTextView.text = bookName
        binding.detailAuthorTextView.text = author
        binding.detailDescriptionTextView.text = description

        binding.loanButton.setOnClickListener {
            // Handle loan button click, navigate to the loan activity
            val intent = Intent(this@TrendingActivity, LoanActivity::class.java)
            intent.putExtra("imgUrl", imageUrl)
            intent.putExtra("name", bookName)
            intent.putExtra("author", author)
            startActivity(intent)
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
}