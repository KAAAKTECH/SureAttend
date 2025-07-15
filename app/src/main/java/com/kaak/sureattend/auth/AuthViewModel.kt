package com.kaak.sureattend.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kaak.sureattend.dataclass.User

class AuthViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun handleUserSignIn(uid: String, email: String, onComplete: () -> Unit) {
        val userRef = firestore.collection("Users").document(uid)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // User already exists, proceed
                onComplete()
            } else {
                // Create new user document
                val newUser = User(
                    name = "Admin", // Hardcoded for testing
                    id = "123456",  // Hardcoded institution ID
                    email = email,
                    hostedCLasses = emptyList(),
                    joinedCLasses = emptyList()
                )

                userRef.set(newUser)
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e -> e.printStackTrace() }
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}