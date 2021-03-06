package com.example.letsgo.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.letsgo.R
import com.example.letsgo.activities.MainActivityViewModel
import com.example.letsgo.constantes.LOGIN_PERMISSION
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private lateinit var mFirebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            verificar()
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private fun verificar() {
        if (mFirebaseAuth.currentUser != null) {
            findNavController().navigate(
                R.id.nav_mapa, bundleOf(), NavOptions.Builder()
                    .setPopUpTo(
                        R.id.nav_PantallaPrincipal,
                        false
                    ).build()
            )
        } else {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTheme(R.style.LoginTheme)
//                    .setLogo(R.drawable.logo2)
                    .setIsSmartLockEnabled(false)
                    .build(),
                LOGIN_PERMISSION
            )
        }
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_PERMISSION) {
            FirebaseFirestore.getInstance().document("/usuarios/${mFirebaseAuth.currentUser?.uid}")
                .set(hashMapOf("fechaCreated" to FieldValue.serverTimestamp()))
            findNavController().navigate(
                R.id.nav_mapa, bundleOf(), NavOptions.Builder()
                    .setPopUpTo(
                        R.id.nav_mapa,
                        false
                    ).build()
            )
        } else if (resultCode == Activity.RESULT_CANCELED) {
            findNavController().navigateUp()
        }
    }

    fun tienePermisos(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
                )
    }
}

