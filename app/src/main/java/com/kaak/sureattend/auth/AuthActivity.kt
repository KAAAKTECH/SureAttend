package com.kaak.sureattend.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kaak.sureattend.ClassActivity
import com.kaak.sureattend.R
import com.kaak.sureattend.databinding.ActivityAuthBinding
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        credentialManager = CredentialManager.create(this)

        binding.googleAuthButton.root.setOnClickListener {
            launchGoogleSignIn()
        }
    }

    private fun launchGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.google_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@AuthActivity
                )

                // Correct usage of GoogleIdTokenCredential
                val googleCred = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleCred.idToken

                if (idToken.isEmpty().not()) {
                    signInToFirebase(idToken)
                } else {
                    Log.e("AuthActivity", "ID token missing")
                }

            } catch (e: GetCredentialException) {
                Log.e("AuthActivity", "Credential Manager error", e)
            }
        }
    }

    private fun signInToFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user?.uid.orEmpty()
                    val email = user?.email.orEmpty()
                    val name = user?.displayName ?: "Admin"

                    viewModel.handleUserSignIn(uid, email) {
                        startActivity(Intent(this, ClassActivity::class.java))
                        finish()
                    }
                } else {
                    Log.e("AuthActivity", "Firebase sign-in failed", task.exception)
                }
            }
    }
}