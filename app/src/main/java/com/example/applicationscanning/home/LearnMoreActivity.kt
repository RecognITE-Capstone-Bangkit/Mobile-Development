package com.example.applicationscanning.home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.R

class LearnMoreActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_more)

        listView = findViewById(R.id.listViewPasal)
        btnBack = findViewById(R.id.btnBack)

        val pasalList = listOf(
            "UU ITE Pasal 27 Ayat 3",
            "UU ITE Pasal 28 Ayat 2",
            "UU ITE Pasal 27 Ayat 2"
        )

        btnBack.setOnClickListener {
            onBackPressed() // Handle back button press
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, pasalList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> navigateToDetail("UU ITE Pasal 27 Ayat 3", getString(R.string.pasal_27_ayat_3))
                1 -> navigateToDetail("UU ITE Pasal 28 Ayat 2", getString(R.string.pasal_28_ayat_2))
                2 -> navigateToDetail("UU ITE Pasal 27 Ayat 2", getString(R.string.pasal_27_ayat_2))
            }
        }
    }

    private fun navigateToDetail(title: String, content: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("title", title)
            putExtra("content", content)
        }
        startActivity(intent)
    }
}
