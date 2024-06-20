package com.example.applicationscanning.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.R
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException

class EditActivity : AppCompatActivity() {

    private lateinit var btnCrop: Button
    private lateinit var selectedImage: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        btnCrop = findViewById(R.id.btnCrop)
        selectedImage = findViewById(R.id.selectedImage)

        imageUri = Uri.parse(intent.getStringExtra("imageUri"))
        selectedImage.setImageURI(imageUri)

        btnCrop.setOnClickListener {
            imageUri?.let { uri ->
                val intent = Intent(this, CropActivity::class.java).apply {
                    putExtra("imageUri", uri.toString())
                }
                startActivityForResult(intent, CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val resultUri = Uri.parse(data?.getStringExtra("croppedImageUri"))
            Log.d("EditActivity", "Cropped image URI: $resultUri")
            if (resultUri != null) {
                selectedImage.setImageURI(resultUri)
                extractTextFromImage(resultUri)
            } else {
                Toast.makeText(this, "Failed to get cropped image URI", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractTextFromImage(imageUri: Uri) {
        val image: Bitmap
        try {
            image = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val textRecognizer = TextRecognizer.Builder(applicationContext).build()
            if (!textRecognizer.isOperational) {
                Log.w("EditActivity", "Text recognizer dependencies are not yet downloaded")
            } else {
                val frame = Frame.Builder().setBitmap(image).build()
                val items = textRecognizer.detect(frame)
                val extractedText = StringBuilder()
                for (i in 0 until items.size()) {
                    val item = items.valueAt(i)
                    extractedText.append(item.value)
                    extractedText.append("\n")
                }
                Log.d("EditActivity", "Extracted Text: $extractedText")
                val intent = Intent().apply {
                    putExtra("extractedText", extractedText.toString())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } catch (e: IOException) {
            Log.e("EditActivity", "Image file error", e)
            Toast.makeText(this, "Image file error", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 1
    }
}
