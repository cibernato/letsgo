package com.example.letsgo.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.example.letsgo.R
import kotlinx.android.synthetic.main.fragment_lector_qr.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LectorQrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LectorQrFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val QR_REQUEST_CODE = 456
    lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
                        val id = it.text
                        Toast.makeText(requireContext(),"Valor recibido $id",Toast.LENGTH_LONG).show()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LectorQrFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LectorQrFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}