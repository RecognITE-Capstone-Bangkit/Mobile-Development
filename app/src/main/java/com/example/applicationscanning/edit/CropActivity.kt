package com.example.applicationscanning.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.R
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream

class CropActivity : AppCompatActivity() {

    private lateinit var cropImageView: CropImageView
    private lateinit var btnSaveCrop: Button
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        cropImageView = findViewById(R.id.cropImageView)
        btnSaveCrop = findViewById(R.id.btnSaveCrop)

        imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        cropImageView.setImageUriAsync(imageUri)

        btnSaveCrop.setOnClickListener {
            val croppedImage = cropImageView.croppedImage
            val resultUri = getImageUriFromBitmap(croppedImage)
            val resultIntent = Intent().apply {
                putExtra("croppedImageUri", resultUri.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Cropped Image", null)
        return Uri.parse(path)
    }
}
