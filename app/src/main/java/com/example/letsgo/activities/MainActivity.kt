package com.example.letsgo.activities

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.letsgo.R
import com.example.letsgo.constantes.Estado
import com.example.letsgo.db.LetsgoDatabase
import com.example.letsgo.service.ServiceBlutooth
import com.example.letsgo.util.ocultarFab
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 654
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    private lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var vm: MainActivityViewModel
    val TAG = "MainActivity"
    var mBluetoothAdapter: BluetoothAdapter? = null
    var REQUEST_ENABLE_BT_V = 150
    lateinit var db: LetsgoDatabase
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    private val permisos = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = LetsgoDatabase.getInstance(this)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mFirebaseAuth = FirebaseAuth.getInstance()
        vm = ViewModelProvider(this).get(MainActivityViewModel::class.java).apply {
            this.db = this@MainActivity.db
        }
        vm.getUbicaciones()
        if (!checkPermissions(permisos)) {
            ActivityCompat.requestPermissions(
                this,
                permisos,
                PERMISSION_REQUEST_CODE
            )
        }else{
            activarBluetoothService()
        }
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_mapa,
                R.id.nav_detalleUbicacion,
                R.id.nav_lectorQrFragment,
                R.id.nav_configuracion
            ), drawerLayout

        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        uiScope.launch {
            db.bluetoothItemDao.clearAll()
            kotlin.runCatching {
                return@runCatching db.bluetoothItemDao.getLastInserted()
            }.onSuccess {
                runOnUiThread {
                    it.observe(this@MainActivity, Observer {
                        val local =
                            vm.ubicaciones.find { u -> !u.macAsociada.isNullOrEmpty() && it.isNotEmpty() && u.macAsociada == it[0].mac }
                        uiScope.launch(Dispatchers.IO) {
                            delay(10 * 1000)
                            local?.let { ub ->
                                runOnUiThread {
                                    Dialog(this@MainActivity).apply {
                                        setContentView(R.layout.dialog_order_confirmation)
                                        findViewById<Button>(R.id.ord_confirmacion_si)
                                            .setOnClickListener {
                                                navController.navigate(
                                                    R.id.nav_presentacionFragment,
                                                    bundleOf("ubicacion" to ub)
                                                )
                                                this.dismiss()
                                            }
                                        findViewById<Button>(R.id.ord_confirmacion_no)
                                            .setOnClickListener {
                                                this.dismiss()
                                            }
                                    }.show()
                                }
                            }
                        }
                    })
                }
            }.onFailure {
                Log.e("Error", "", it)
            }
        }
    }

    fun activarBluetoothService() {
        if(vm.bluetoothService == Estado.INACTIVO){
            enableDisableBT(true)
        }
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        this.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun checkPermissions(permisions: Array<String>): Boolean {
        var x = true
        permisions.iterator().forEach {
            x = x and (ContextCompat.checkSelfPermission(this, it) == 0)
        }
        return x
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val c = navController.currentDestination?.id
        if (c == R.id.nav_mapa && mFirebaseAuth.currentUser != null) {
            finish()
        }
        if (c == R.id.nav_presentacionFragment) {
            ocultarFab()
        }
        super.onBackPressed()
    }

    fun enableDisableBT(habilitar: Boolean = false) {
        try {
            if (mBluetoothAdapter != null) {
                // Device doesn't support Bluetooth
                if (habilitar) {
//                    if (mBluetoothAdapter?.isEnabled == false) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    val discoverableIntent =
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0)
//                            startActivity(discoverableIntent)
                    startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT_V)
                    //                    }
                } else {
                    Log.d(TAG, "enableDisableBT: disabling BT")
//                    unregisterReceiver(mBroadcastReceiver3)
                    mBluetoothAdapter!!.disable()
                    stopService(Intent(this, ServiceBlutooth::class.java))
                    vm.bluetoothService = Estado.INACTIVO
                }
            } else {
                Log.d(TAG, "No tiene blutooth")
            }
        } catch (e: Exception) {
            // already unregistered
        }
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) {
                val mode =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)
                when (mode) {
//                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled")
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled")
                        startService(Intent(context, ServiceBlutooth::class.java))
                        vm.bluetoothService = Estado.ACTIVO
                    }
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> Log.d(
                        TAG,
                        "mBroadcastReceiver2: Discoverability Enabled, able to receive connections"
                    )
                    BluetoothAdapter.SCAN_MODE_NONE -> {
                        Log.d(
                            TAG,
                            "mBroadcastReceiver2: Discoverability Disabled, not able to receive connections"
                        )
                        stopService(Intent(context, ServiceBlutooth::class.java));

                    }
                    BluetoothAdapter.STATE_CONNECTING -> Log.d(
                        TAG,
                        "mBroadcastReceiver2:  Connecting ..."
                    )
                    BluetoothAdapter.STATE_CONNECTED -> Log.d(
                        TAG,
                        "mBroadcastReceiver2:  Connected ..."
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT_V -> {

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.e("Permisos", "Otorgados")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
