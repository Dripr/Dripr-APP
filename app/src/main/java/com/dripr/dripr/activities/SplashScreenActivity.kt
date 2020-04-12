package com.dripr.dripr.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dripr.dripr.R
import com.dripr.dripr.entities.User

class SplashScreenActivity : AppCompatActivity() {

    private var delayHandler: Handler? = null
    private val delay: Long = 1000

    private val runnable: Runnable = Runnable {
        if (!isFinishing) {
            val tokens = User.getFromDevice(this).tokens
            if (tokens.isEmpty()) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        delayHandler = Handler()
        delayHandler!!.postDelayed(runnable, delay)
    }

    public override fun onDestroy() {
        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(runnable)
        }
        super.onDestroy()
    }
}