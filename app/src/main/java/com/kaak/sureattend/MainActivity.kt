package com.kaak.sureattend

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kaak.sureattend.adapter.ClassAdapter
import com.kaak.sureattend.viewmodel.ClassViewModel

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var navMenu: PopupMenu? = null
    private var isMenuVisible = false
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: ClassAdapter

    private val viewModel: ClassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setupNavMenu()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewClasses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClassAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.classList.observe(this) { updatedList ->
            adapter.updateData(updatedList)
        }

        viewModel.startListeningToClasses()

        findViewById<ImageButton>(R.id.buttonAddClass).setOnClickListener {
            showNewClassDialog()
        }
    }

    private fun showNewClassDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_class, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextListName)
        val createButton = dialogView.findViewById<Button>(R.id.btn_create)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        createButton.setOnClickListener {
            val className = editText.text.toString().trim()
            if (className.isNotEmpty()) {
                viewModel.createClass(className) { success ->
                    Toast.makeText(
                        this,
                        if (success) "Class created successfully!" else "Failed to create class",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            } else {
                editText.error = "Class name cannot be empty"
            }
        }

        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupNavMenu() {
        toolbar.setNavigationOnClickListener {
            if (isMenuVisible) navMenu?.dismiss() else showPopupMenu()
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
        navMenu = PopupMenu(this, toolbar).apply {
            menuInflater.inflate(R.menu.nav_menu, menu)
            setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.nav_settings -> true
                    R.id.nav_about -> true
                    R.id.nav_sign_out -> true
                    else -> false
                }
            }
            setOnDismissListener { isMenuVisible = false }
            show()
            isMenuVisible = true
        }
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
