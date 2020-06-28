package com.example.letsgo.service

import android.app.IntentService
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.letsgo.db.LetsgoDatabase
import com.example.letsgo.db.models.BluetoothItem
import com.example.letsgo.util.toISOString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class ServiceBlutooth : IntentService("ServiceBlutooth") {

    private val TAG = "MainActivity"
    var mBluetoothAdapter: BluetoothAdapter? = null
    var handler: Handler = Handler()
    lateinit var bm : LocalBroadcastManager
    lateinit var db :LetsgoDatabase
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    override fun onCreate() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        db = LetsgoDatabase.getInstance(this)
        bm = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Log.d("Handlers", "Se inicia")
            handler.post(runnableCode)
        } catch (e: Exception) {
            // already unregistered
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnableCode)
        unregisterReceiver(mReceiver)
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onHandleIntent(p0: Intent?) {
    }

    private val runnableCode: Runnable = object : Runnable {
        override fun run() {
//            Toast.makeText(context, "Hola soy un Toast", Toast.LENGTH_SHORT).show();
            btnDiscover()
            handler.postDelayed(this, 60000)
        }
    }
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "mReceiver: FOUND ...")
            val action = intent.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    Log.e(
                        TAG, """
                         ${it.name}
                         ${it.address}
                         """.trimIndent()
                    )
                    uiScope.launch {
                        db.bluetoothItemDao.insert(BluetoothItem(it.address,it.name, Date().toISOString()))
                    }
                }
            } else {
                Toast.makeText(context, "No se encontro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun btnDiscover() {
        if (mBluetoothAdapter!!.isDiscovering) {
            mBluetoothAdapter!!.cancelDiscovery()
            Log.d(TAG, "btnDiscover: canceling discovering ...")
            //            checkBTPermissions()
            mBluetoothAdapter!!.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(mReceiver, filter)
        }
        if (!mBluetoothAdapter!!.isDiscovering) {
            Log.d(TAG, "btnDiscover: init dico...")
            //            checkBTPermissions()
            mBluetoothAdapter!!.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(mReceiver, filter)
        }
    }

}