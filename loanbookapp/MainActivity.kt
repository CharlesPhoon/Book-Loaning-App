package com.example.loanbookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.loanbookapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageContainer: LinearLayout
    private lateinit var imagesRef: DatabaseReference
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchView = binding.searchView
        imageContainer = binding.imageContainer
        imagesRef = FirebaseDatabase.getInstance().getReference("Books")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission (if needed)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text change
                filterBooksByName(newText)
                return true
            }
        })

        imagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val imageData = childSnapshot.getValue(Book::class.java)
                    val imageUrl = imageData?.imgUrl

                    if (imageUrl != null){
                        val imageView = ImageView(this@MainActivity)
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            resources.getDimensionPixelSize(R.dimen.image_width),
                            resources.getDimensionPixelSize(R.dimen.image_height)
                        )
                        Glide.with(this@MainActivity)
                        .load(imageUrl)
                        .into(imageView)

                        imageView.setOnClickListener {
                            val intent = Intent(this@MainActivity, TrendingActivity::class.java)
                            intent.putExtra("imgUrl", imageUrl)
                            intent.putExtra("name", imageData.name)
                            intent.putExtra("author", imageData.author)
                            intent.putExtra("description", imageData.description)
                            startActivity(intent)
                        }

                        imageContainer.addView(imageView)
                    }
                }
                binding.mysteryBtn.setOnClickListener { filterBooksByCategory("Mystery") }
                binding.fantasyBtn.setOnClickListener { filterBooksByCategory("Fantasy") }
                binding.horrorBtn.setOnClickListener { filterBooksByCategory("Horror") }
                binding.adventureBtn.setOnClickListener { filterBooksByCategory("Adventure") }
                binding.fictionBtn.setOnClickListener { filterBooksByCategory("Fiction") }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
                Toast.makeText(applicationContext, "Database error: $(error.message)", Toast.LENGTH_SHORT).show()
            }
        })
        bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
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
    private fun filterBooksByCategory(category: String) {
        // Clear existing views in the container
        imageContainer.removeAllViews()
        imagesRef.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val imageData = childSnapshot.getValue(Book::class.java)
                    val imageUrl = imageData?.imgUrl

                    if (imageUrl != null) {
                        val imageView = ImageView(this@MainActivity)
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            resources.getDimensionPixelSize(R.dimen.image_width),
                            resources.getDimensionPixelSize(R.dimen.image_height)
                        )
                        Glide.with(this@MainActivity)
                            .load(imageUrl)
                            .into(imageView)

                        imageView.setOnClickListener {
                            val intent = Intent(this@MainActivity, TrendingActivity::class.java)
                            intent.putExtra("imgUrl", imageUrl)
                            intent.putExtra("name", imageData.name)
                            intent.putExtra("author", imageData.author)
                            intent.putExtra("description", imageData.description)
                            startActivity(intent)
                        }

                        imageContainer.addView(imageView)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
                Toast.makeText(applicationContext, "Database error: $(error.message)", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterBooksByName(bookName: String?) {
        // Clear existing views in the container
        imageContainer.removeAllViews()

        // Check if the search query is not null or empty
        if (!bookName.isNullOrBlank()) {
            val lowercaseBookName = bookName.lowercase()
            imagesRef.orderByChild("nameS")
                .startAt(lowercaseBookName)
                .endAt(lowercaseBookName + "\uf8ff")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val imageData = childSnapshot.getValue(Book::class.java)
                            val imageUrl = imageData?.imgUrl

                            if (imageUrl != null) {
                                val imageView = ImageView(this@MainActivity)
                                imageView.layoutParams = LinearLayout.LayoutParams(
                                    resources.getDimensionPixelSize(R.dimen.image_width),
                                    resources.getDimensionPixelSize(R.dimen.image_height)
                                )
                                Glide.with(this@MainActivity)
                                    .load(imageUrl)
                                    .into(imageView)

                                imageView.setOnClickListener {
                                    val intent = Intent(this@MainActivity, TrendingActivity::class.java)
                                    intent.putExtra("imgUrl", imageUrl)
                                    intent.putExtra("name", imageData.name)
                                    intent.putExtra("author", imageData.author)
                                    intent.putExtra("description", imageData.description)
                                    startActivity(intent)
                                }

                                imageContainer.addView(imageView)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Database error: ${error.message}")
                        Toast.makeText(
                            applicationContext,
                            "Database error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            // If the search query is empty, show all books
            imagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val imageData = childSnapshot.getValue(Book::class.java)
                        val imageUrl = imageData?.imgUrl

                        if (imageUrl != null) {
                            val imageView = ImageView(this@MainActivity)
                            imageView.layoutParams = LinearLayout.LayoutParams(
                                resources.getDimensionPixelSize(R.dimen.image_width),
                                resources.getDimensionPixelSize(R.dimen.image_height)
                            )
                            Glide.with(this@MainActivity)
                                .load(imageUrl)
                                .into(imageView)

                            imageView.setOnClickListener {
                                val intent = Intent(this@MainActivity, TrendingActivity::class.java)
                                intent.putExtra("imgUrl", imageUrl)
                                intent.putExtra("name", imageData.name)
                                intent.putExtra("author", imageData.author)
                                intent.putExtra("description", imageData.description)
                                startActivity(intent)
                            }

                            imageContainer.addView(imageView)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Database error: ${error.message}")
                    Toast.makeText(applicationContext, "Database error: $(error.message)", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}