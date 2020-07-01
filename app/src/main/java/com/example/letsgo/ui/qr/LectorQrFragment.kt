package com.example.letsgo.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.example.letsgo.R
import com.example.letsgo.models.Ubicacion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_lector_qr.*

class LectorQrFragment : Fragment() {
    val QR_REQUEST_CODE = 456
    lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                50
            ); }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), QR_REQUEST_CODE)
        }
        return inflater.inflate(R.layout.fragment_lector_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (tienePermiso()) {
            try {
                if (!this::codeScanner.isInitialized) {
                    codeScanner = CodeScanner(requireContext(), scanner_view)
                }
                codeScanner.setDecodeCallback {
                    activity?.runOnUiThread {
                        val ubicacionString = it.text
                        if (ubicacionString.startsWith("{")) {
                            val ub = Gson().fromJson<Ubicacion>(ubicacionString,
                                object : TypeToken<Ubicacion>() {}.type)!!
                            findNavController().navigate(R.id.nav_presentacionFragment, bundleOf("ubicacion" to ub))
                        }else{
                            Toast.makeText(requireContext(),"No es un QR valido",Toast.LENGTH_LONG).show()
                        }
                        codeScanner.startPreview()
                    }
                }
                codeScanner.startPreview()
            } catch (e: Exception) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), QR_REQUEST_CODE)
            }
        } else {
            Toast.makeText(requireContext(), "Se requiere permisos", Toast.LENGTH_LONG).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        if (tienePermiso()) {
            codeScanner.releaseResources()
        } else {
            Toast.makeText(requireContext(), "Se requiere permisos", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), QR_REQUEST_CODE)
        } else {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == QR_REQUEST_CODE) {
            permissions.forEachIndexed { index, s ->
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    val rechazado = shouldShowRequestPermissionRationale(s)
                    if (!rechazado) {
                        activity?.finish()
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), QR_REQUEST_CODE)
                    }
                }
            }
        }

    }

    fun tienePermiso(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

}