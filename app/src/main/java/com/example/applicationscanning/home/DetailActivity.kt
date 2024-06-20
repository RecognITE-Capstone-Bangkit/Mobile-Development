package com.example.applicationscanning.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")

        binding.tvTitle.text = title
        binding.tvContent.text = content

        binding.btnBack.setOnClickListener {
            onBackPressed() // Handle back button press
        }
    }
}