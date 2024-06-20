package com.example.applicationscanning

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationscanning.home.MainActivity
import com.example.applicationscanning.home.WelcomeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Duration for which the splash screen will be shown
        val splashScreenTime = 3000L // 3 seconds

        Handler(Looper.getMainLooper()).postDelayed({
            // Start MainActivity after the splash screen duration
            val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, splashScreenTime)
    }
}