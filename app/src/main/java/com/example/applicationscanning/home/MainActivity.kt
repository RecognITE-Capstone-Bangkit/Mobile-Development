package com.example.applicationscanning.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.applicationscanning.bookmark.BookmarkActivity
import com.example.applicationscanning.coment.ComentActivity
import com.example.applicationscanning.databinding.ActivityMainBinding
import com.example.applicationscanning.edit.EditActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Log.d("MainActivity", "Selected imageUri: $imageUri")
            val intent = Intent(this, EditActivity::class.java).apply {
                putExtra("imageUri", imageUri.toString())
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT)
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getImageFromGallery.launch("image/*")
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBookmark.setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))
        }

        binding.btnPaste.setOnClickListener {
            startActivity(Intent(this, ComentActivity::class.java))
        }

        binding.btnScreenShoot.setOnClickListener {
            checkPermissionsAndOpenGallery()
        }

        binding.btnLearnMore.setOnClickListener {
            startActivity(Intent(this, LearnMoreActivity::class.java))
        }

    }

    private fun checkPermissionsAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getImageFromGallery.launch("image/*")
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getImageFromGallery.launch("image/*")
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == Activity.RESULT_OK) {
            val extractedText = data?.getStringExtra("extractedText")
            val intent = Intent(this, ComentActivity::class.java).apply {
                putExtra("extractedText", extractedText)
            }
            startActivity(intent)
        }
    }

    companion object {
        const val REQUEST_CODE_EDIT = 1001
    }

}
