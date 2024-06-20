package com.example.applicationscanning.coment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.R
import com.example.applicationscanning.bookmark.BookmarkActivity
import com.example.applicationscanning.databinding.ActivityComentBinding
import com.example.applicationscanning.databinding.DialogResultSafeBinding
import com.example.applicationscanning.databinding.DialogResultViolanceBinding
import com.example.applicationscanning.result.HasilActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ComentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComentBinding
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed() // Handle back button press
        }
        editText = binding.editText

        val extractedText = intent.getStringExtra("extractedText")
        Log.d("ComentActivity", "Extracted text: $extractedText")
        editText.setText(extractedText)

        binding.btnSubmit.setOnClickListener {
            val inputText = binding.editText.text.toString()
            if (inputText.isNotEmpty()) {
                sendTextToApi(inputText)
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTextToApi(text: String) {
        binding.progressBar.visibility = View.VISIBLE // Show loading indicator
        CoroutineScope(Dispatchers.IO).launch {
            val request = PredictRequest(text)
            try {
                val response = RetrofitInstance.api.predict(request)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE // Hide loading indicator
                    handleApiResponse(response.predicted_label, text)
                }
            } catch (e: IOException) {
                Log.e("ComentActivity", "Network error", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE // Hide loading indicator
                    Toast.makeText(this@ComentActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Log.e("ComentActivity", "Server error", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE // Hide loading indicator
                    Toast.makeText(this@ComentActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleApiResponse(predictedLabel: String, inputText: String) {
        val resultText = when (predictedLabel) {
            "0" -> "Netral"
            "1" -> "Cyberbullying"
            "2" -> "Hatespeech"
            "3" -> "Judi Online"
            else -> "Unknown"
        }
        showResultDialog(resultText, inputText)
    }

    private fun showResultDialog(resultText: String, inputText: String) {
        val dialogView = if (resultText == "Netral") {
            layoutInflater.inflate(R.layout.dialog_result_safe, null)
        } else {
            layoutInflater.inflate(R.layout.dialog_result_violance, null)
        }

        val imageViewIcon = dialogView.findViewById<ImageView>(R.id.imageViewIcon)
        val textViewResult = dialogView.findViewById<TextView>(R.id.textViewResult)
        val textViewITE = dialogView.findViewById<TextView>(R.id.textViewITE)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)
        val textViewBack = dialogView.findViewById<TextView>(R.id.textViewBack)

        // Set the result text directly
        textViewResult.text = resultText

        // Set the icon based on the result (if needed)
        val iconRes = when (resultText) {
            "Netral" -> R.drawable.ic_check
            "Cyberbullying", "Hatespeech", "Judi Online" -> R.drawable.ic_dangerous
            else -> R.drawable.ic_unknown
        }
        imageViewIcon.setImageResource(iconRes)

        // Set the ITE text based on the result
        val iteText = when (resultText) {
            "Netral" -> "Teks Anda bersifat netral dan tidak mengandung unsur yang melanggar Undang-Undang Informasi dan Transaksi Elektronik."
            "Cyberbullying" -> "Teks Anda berpotensi melanggar undang-undang terkait cyberbullying berdasarkan UU ITE Pasal 27 ayat (3), yang melarang distribusi informasi elektronik yang memiliki muatan penghinaan atau pencemaran nama baik."
            "Hatespeech" -> "Teks Anda berpotensi melanggar undang-undang terkait ujaran kebencian berdasarkan UU ITE Pasal 28 ayat (2), yang melarang penyebaran informasi untuk menimbulkan rasa kebencian atau permusuhan individu dan/atau kelompok masyarakat tertentu berdasarkan atas suku, agama, ras, dan antar golongan (SARA)."
            "Judi Online" -> "Teks Anda berpotensi melanggar undang-undang terkait perjudian online berdasarkan UU ITE Pasal 27 ayat (2), yang melarang distribusi informasi elektronik yang memiliki muatan perjudian."
            else -> ""
        }
        textViewITE.text = iteText

        val builder = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        dialogView.startAnimation(bounceAnimation)

        buttonSave.setOnClickListener {
            saveToBookmark(resultText, inputText)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        textViewBack.setOnClickListener {
            // Handle back to home text click
            Toast.makeText(this, "Back to Home", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            finish() // Finish the activity to go back to home
        }

        alertDialog.show()
    }

    private fun saveToBookmark(resultText: String?, inputText: String?) {
        val sharedPreferences = getSharedPreferences("bookmark_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing bookmarks or create a new set if it doesn't exist
        val existingBookmarks = sharedPreferences.getStringSet("bookmarks", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        // Add the new bookmark
        val newBookmark = "Input: $inputText\nResult: $resultText"
        existingBookmarks.add(newBookmark)

        // Save the updated bookmarks
        editor.putStringSet("bookmarks", existingBookmarks)
        editor.apply()

        // Navigate to the BookmarkActivity
        val intent = Intent(this, BookmarkActivity::class.java)
        startActivity(intent)
    }
}
