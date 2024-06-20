package com.example.applicationscanning.result

import com.example.applicationscanning.R
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.bookmark.BookmarkActivity
import com.example.applicationscanning.databinding.ActivityHasilBinding
import com.example.applicationscanning.home.MainActivity

class HasilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHasilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("result")
        val inputText = intent.getStringExtra("inputText")
        binding.tvResult.text = result

        if (result == "Netral") {
            binding.imgResult.setImageResource(R.drawable.ic_dangerous)
            binding.tvResult.setBackgroundColor(Color.parseColor("#B76F6F"))
            binding.btnSave.setBackgroundColor(Color.parseColor("#8B0000"))
        } else {
            binding.imgResult.setImageResource(R.drawable.ic_check)
            binding.tvResult.setBackgroundColor(Color.parseColor("#8B0000"))
            binding.btnSave.setBackgroundColor(Color.parseColor("#B76F6F"))
        }

        binding.btnSave.setOnClickListener {
            saveToBookmark(result, inputText)
        }

        binding.backToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveToBookmark(result: String?, inputText: String?) {
        val sharedPreferences = getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing bookmarks or create a new set if it doesn't exist
        val existingBookmarks = sharedPreferences.getStringSet("bookmarks", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Add the new bookmark
        val newBookmark = "Input: $inputText\nResult: $result"
        existingBookmarks.add(newBookmark)

        // Save the updated bookmarks
        editor.putStringSet("bookmarks", existingBookmarks)
        editor.apply()

        // Navigate to the BookmarkActivity
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
    }
}
