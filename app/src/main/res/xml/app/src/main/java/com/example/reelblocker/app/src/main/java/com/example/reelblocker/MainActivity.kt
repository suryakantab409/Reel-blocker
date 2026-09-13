package com.example.reelblocker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val infoText = TextView(this).apply {
            text = "Reel Blocker Setup\n\n1. Niche button dabayein.\n2. Accessibility me jakar 'Reel Blocker' ko ON karein.\n3. Har 30 Reels ke baad app 1 ghante ke liye block kar degi."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }

        val button = Button(this).apply {
            text = "Service ON Karein"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        layout.addView(infoText)
        layout.addView(button)
        setContentView(layout)
    }
}
