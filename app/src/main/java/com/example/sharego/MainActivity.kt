package com.example.sharego

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sharego.dataClasses.Usuario
import com.example.sharego.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = Firebase.firestore

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val userID = firebaseUser.uid
                db.collection("Usuarios")
                    .document(userID)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val usuarioReference = document.reference
                            val usuario = document.toObject(Usuario::class.java)!!
                            UserManager.setUsuario(usuario)
                            UserManager.setUsuarioReference(usuarioReference)
                        } else {
                            // El usuario no existe en Firestore, así que lo creamos
                            val newUser = Usuario(
                                nombre = firebaseUser.displayName ?: "",
                                email = firebaseUser.email ?: ""
                                // otros campos que necesites inicializar
                            )
                            db.collection("Usuarios").document(userID).set(newUser)
                                .addOnSuccessListener {
                                    UserManager.setUsuario(newUser)
                                    UserManager.setUsuarioReference(db.collection("Usuarios").document(userID))
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirestoreError", "Error al crear el usuario en Firestore", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreError", "Error al obtener el usuario de Firestore", e)
                    }
            }
        } else {
            Log.e("AuthError", "Error en la autenticación: ${response?.error?.errorCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()

        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher_foreground) // Set logo drawable
            .setTheme(R.style.Theme_ShareGo) // Set theme
            .build()
        signInLauncher.launch(signInIntent)

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_publish, R.id.navigation_search, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        hideSystemUI()
        super.onResume()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}