package com.example.applicationscanning.bookmark

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.R
import com.example.applicationscanning.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        loadBookmarks()
    }

    private fun loadBookmarks() {
        val sharedPreferences = getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
        val savedBookmarks = sharedPreferences.getStringSet("bookmarks", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        binding.linearLayoutBookmarks.removeAllViews() // Bersihkan tampilan yang ada
        savedBookmarks.forEach { bookmark ->
            val bookmarkView = createBookmarkView(bookmark, savedBookmarks)
            binding.linearLayoutBookmarks.addView(bookmarkView)
        }
    }

    private fun createBookmarkView(bookmark: String, savedBookmarks: MutableSet<String>): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        }

        val (inputText, resultText) = bookmark.split("\nResult: ")

        val horizontalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val inputTextView = TextView(this).apply {
            text = inputText
            textSize = 16f
        }

        val resultTextView = TextView(this).apply {
            text = resultText
            textSize = 25f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        textLayout.addView(inputTextView)
        textLayout.addView(resultTextView)

        val deleteButton = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_delete)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                showDeleteConfirmationDialog(bookmark, savedBookmarks)
            }
        }

        horizontalLayout.addView(textLayout)
        horizontalLayout.addView(deleteButton)

        val separator = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            setBackgroundColor(resources.getColor(R.color.gray, null))
        }

        layout.addView(horizontalLayout)
        layout.addView(separator)

        return layout
    }

    private fun showDeleteConfirmationDialog(bookmark: String, savedBookmarks: MutableSet<String>) {
        val dialogView = layoutInflater.inflate(R.layout.delete_confirmation_alert, null)
        val builder = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
        builder.setView(dialogView)

        val positiveButton = dialogView.findViewById<Button>(R.id.buttonDelete)
        val negativeButton = dialogView.findViewById<Button>(R.id.buttonNotNow)

        val alertDialog = builder.create()

        positiveButton.setOnClickListener {
            removeBookmark(bookmark, savedBookmarks)
            alertDialog.dismiss()
        }
        negativeButton.setOnClickListener{
            alertDialog.dismiss()
        }

        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        dialogView.startAnimation(bounceAnimation)

        alertDialog.show()


    }

    private fun removeBookmark(bookmark: String, savedBookmarks: MutableSet<String>) {
        val sharedPreferences = getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        savedBookmarks.remove(bookmark)
        editor.putStringSet("bookmarks", savedBookmarks)
        editor.apply()

        // Refresh daftar bookmark
        binding.linearLayoutBookmarks.removeAllViews()
        loadBookmarks()
    }
}
