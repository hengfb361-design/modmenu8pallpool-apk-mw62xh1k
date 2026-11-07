package com.example.poolfloatingoverlay

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val requestOverlay = 701
    private val poolPackageName = "com.miniclip.eightballpool"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(36, 36, 36, 36)
            setBackgroundColor(Color.rgb(20, 25, 35))
        }

        val title = TextView(this).apply {
            text = "Pool Floating Tools"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val info = TextView(this).apply {
            text = "ជំនួយការគណនាបន្ទាត់តម្រង់ Auto Aim + ផ្លូវកាត់បើកហ្គេម 8 Ball Pool"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 24)
        }

        val startOverlay = Button(this).apply {
            text = "១. បើក Floating Auto Aim"
            setBackgroundColor(Color.rgb(35, 135, 75))
            setTextColor(Color.WHITE)
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    Toast.makeText(this@MainActivity, "បានបើក Floating Tool រួចរាល់", Toast.LENGTH_SHORT).show()
                } else {
                    val uri = Uri.parse("package:$packageName")
                    startActivityForResult(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri),
                        requestOverlay
                    )
                }
            }
        }

        val launchGame = Button(this).apply {
            text = "២. ចូលលេង 8 Ball Pool"
            setBackgroundColor(Color.rgb(25, 110, 190))
            setTextColor(Color.WHITE)
            setOnClickListener {
                // បើក Floating Overlay ស្វ័យប្រវត្តិនឹងបើកហ្គេម 8 Ball Pool
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                }
                launch8BallPool()
            }
        }

        val stopOverlay = Button(this).apply {
            text = "បិទ Floating Tool"
            setBackgroundColor(Color.rgb(180, 50, 50))
            setTextColor(Color.WHITE)
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
                Toast.makeText(this@MainActivity, "បានបិទ Floating Tool", Toast.LENGTH_SHORT).show()
            }
        }

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 12, 0, 12)
        }

        root.addView(title)
        root.addView(info)
        root.addView(startOverlay, lp)
        root.addView(launchGame, lp)
        root.addView(stopOverlay, lp)

        setContentView(root)
    }

    private fun launch8BallPool() {
        val pm = packageManager
        val intent = pm.getLaunchIntentForPackage(poolPackageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            // ប្រសិនបើមិនទាន់មានហ្គេម 8 Ball Pool ក្នុងទូរស័ព្ទ បើកទៅ Play Store
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$poolPackageName")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$poolPackageName")))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestOverlay && Settings.canDrawOverlays(this)) {
            startService(Intent(this, OverlayService::class.java))
        }
    }
}
