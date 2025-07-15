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
import android.widget.TextView
import android.view.View
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout


@Suppress("DEPRECATION")
class ClassActivity : AppCompatActivity() {

    private var navMenu: PopupMenu? = null
    private var isMenuVisible = false
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: ClassAdapter

    private val viewModel: ClassViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class)

        toolbar = findViewById(R.id.toolbar)
        setupNavMenu()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewClasses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClassAdapter(
            emptyList(),
            onItemLongClick = { item, _ -> viewModel.toggleSelection(item) },
            onItemClick = { item, _ ->
                if ((viewModel.selectedClasses.value?.isNotEmpty()) == true) {
                    viewModel.toggleSelection(item)
                } else {
                    // Normal (non-selection) click action if any
                }
            },
            isItemSelected = { item -> viewModel.selectedClasses.value?.contains(item) == true }
        )

        recyclerView.adapter = adapter

        viewModel.classList.observe(this) { updatedList ->
            adapter.updateData(updatedList)
        }

        viewModel.selectedClasses.observe(this) { selectedItems ->
            val selectionCount = selectedItems.size
            isMenuVisible = false
            navMenu?.dismiss()
            updateSelectionUI(selectionCount, selectedItems.size == 1)
            adapter.notifyDataSetChanged()
        }


        viewModel.startListeningToClasses()

        findViewById<ImageButton>(R.id.buttonAddClass).setOnClickListener {
            showNewClassDialog()
        }
    }

    private fun showNewClassDialog() {
        // Inflate the layout for the dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_class, null)

        // Find views in the dialog layout
        val editText = dialogView.findViewById<EditText>(R.id.editTextListName)
        val createButton = dialogView.findViewById<Button>(R.id.btn_create)

        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Set dialog background to transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set the button click listener
        createButton.setOnClickListener {
            val className = editText.text.toString().trim()

            // Check if class name is empty
            if (className.isNotEmpty()) {
                // Create class using ViewModel
                viewModel.createClass(className) { success ->
                    // Show a success or failure message in a Toast
                    Toast.makeText(
                        this,
                        if (success) "Class created successfully!" else "Failed to create class",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // Dismiss the dialog once the class is created
                dialog.dismiss()
            } else {
                // Show an error message if class name is empty
                editText.error = "Class name cannot be empty"
            }
        }

        // Show the dialog
        dialog.show()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupNavMenu() {
        toolbar.setNavigationOnClickListener {
            if (isMenuVisible) navMenu?.dismiss() else showPopupMenu()
        }

        findViewById<ConstraintLayout>(R.id.main)
            .setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (isMenuVisible) {
                        navMenu?.dismiss()
                        return@setOnTouchListener true
                    } else if (viewModel.selectedClasses.value?.isNotEmpty() == true) {
                        viewModel.clearSelection()
                        return@setOnTouchListener true
                    }
                }
                false
            }
    }

    private fun showPopupMenu() {
        val anchor = findViewById<View>(R.id.navButtonAnchor)

        navMenu = PopupMenu(this, anchor, Gravity.START).apply {
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

    @SuppressLint("SetTextI18n")
    private fun updateSelectionUI(count: Int, canEdit: Boolean) {
        val titleText = findViewById<TextView>(R.id.textViewTitle)
        val userText = findViewById<TextView>(R.id.textViewUserName)

        // Check if any classes are selected
        if (count > 0) {
            titleText.text = "$count selected"
            userText.visibility = View.GONE

            // Dynamically add the buttons if they aren't already added
            if (findViewById<ImageButton>(R.id.buttonDelete) == null) {
                // Create Delete Button
                val deleteBtn = ImageButton(this).apply {
                    id = R.id.buttonDelete
                    setImageResource(R.drawable.ic_delete)
                    setBackgroundColor(Color.TRANSPARENT)
                    setOnClickListener {
                        viewModel.deleteSelectedClasses { success ->
                            Toast.makeText(
                                this@ClassActivity,
                                if (success) "Deleted successfully" else "Failed to delete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // Create Edit Button
                val editBtn = ImageButton(this).apply {
                    id = R.id.buttonEdit
                    setImageResource(R.drawable.ic_edit)
                    setBackgroundColor(Color.TRANSPARENT)
                    visibility = if (canEdit) View.VISIBLE else View.GONE
                    setOnClickListener {
                        // Trigger edit dialog in ViewModel (to be implemented)
                        Toast.makeText(this@ClassActivity, "Edit clicked", Toast.LENGTH_SHORT).show()
                    }
                }

                // Add the buttons to the layout
                val layout = findViewById<ConstraintLayout>(R.id.main)
                layout.addView(deleteBtn)
                layout.addView(editBtn)

                val paramsDelete = ConstraintLayout.LayoutParams(100, 100).apply {
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    topToTop = R.id.toolbar
                    bottomToBottom = R.id.toolbar
                    marginEnd = 8  // Optional spacing
                }
                deleteBtn.layoutParams = paramsDelete

                val paramsEdit = ConstraintLayout.LayoutParams(100, 100).apply {
                    endToStart = R.id.buttonDelete
                    topToTop = R.id.toolbar
                    bottomToBottom = R.id.toolbar
                    marginEnd = 8
                }
                editBtn.layoutParams = paramsEdit

                editBtn.layoutParams = paramsEdit
            } else {
                // If buttons already exist, just update the visibility of the edit button
                findViewById<ImageButton>(R.id.buttonEdit).visibility = if (canEdit) View.VISIBLE else View.GONE
            }
        } else {
            titleText.text = "SureAttend"
            userText.visibility = View.VISIBLE

            // Remove the buttons when no items are selected
            findViewById<ImageButton>(R.id.buttonEdit)?.let {
                (it.parent as ViewGroup).removeView(it)
            }
            findViewById<ImageButton>(R.id.buttonDelete)?.let {
                (it.parent as ViewGroup).removeView(it)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (isMenuVisible) {
            navMenu?.dismiss()
        } else if (viewModel.selectedClasses.value?.isNotEmpty() == true) {
            viewModel.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

}
