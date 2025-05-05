package com.kaak.sureattend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var navMenu: PopupMenu? = null
    private var isMenuVisible = false
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize toolbar and menu
        toolbar = findViewById(R.id.toolbar)
        setupNavMenu()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupNavMenu() {
        toolbar.setNavigationOnClickListener {
            if (isMenuVisible) {
                navMenu?.dismiss()
            } else {
                showPopupMenu()
            }
        }

        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
            .setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN && isMenuVisible) {
                    navMenu?.dismiss()
                }
                false
            }
    }

    private fun showPopupMenu() {
        navMenu = PopupMenu(this, toolbar)
        navMenu?.menuInflater?.inflate(R.menu.nav_menu, navMenu?.menu)

        navMenu?.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    // TODO: Navigate to Settings
                    true
                }
                R.id.nav_about -> {
                    // TODO: Show About screen
                    true
                }
                R.id.nav_sign_out -> {
                    // TODO: Sign out logic
                    true
                }
                else -> false
            }
        }

        navMenu?.setOnDismissListener {
            isMenuVisible = false
        }

        isMenuVisible = true
        navMenu?.show()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (isMenuVisible) {
            navMenu?.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}
