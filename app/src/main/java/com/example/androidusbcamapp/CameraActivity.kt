package com.example.androidusbcamapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var tvStatus: TextView
    private lateinit var btnConnectUSB: Button

    // USB Service
    private var usbService: USBCameraService? = null
    private var isUSBBound = false

    private val usbConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("CameraActivity", "USB Service Connected")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("CameraActivity", "USB Service Disconnected")
            usbService = null
            isUSBBound = false
        }
    }

    // Camera permission request code
    private companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        setupViews()
        setupCamera()
        startUSBService()
    }

    private fun setupViews() {
        previewView = findViewById(R.id.previewView)
        tvStatus = findViewById(R.id.tvStatus)
        btnConnectUSB = findViewById(R.id.btnConnectUSB)

        cameraExecutor = Executors.newSingleThreadExecutor()

        btnConnectUSB.setOnClickListener {
            simulatePS4Connection()
        }
    }

    private fun setupCamera() {
        // Check camera permission
        if (hasCameraPermission()) {
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun startUSBService() {
        val intent = Intent(this, USBCameraService::class.java)
        startService(intent)
        bindService(intent, usbConnection, Context.BIND_AUTO_CREATE)

        updateStatus("🔍 Scanning for USB devices...")
    }

    private fun simulatePS4Connection() {
        updateStatus("🎮 Connecting to virtual PS4...")

        // Simulate connection process
        Thread {
            Thread.sleep(1500)
            runOnUiThread {
                updateStatus("✅ Virtual PS4 Connected!")
                Toast.makeText(this, "PS4 Stream: 1920x1080 @ 60fps", Toast.LENGTH_LONG).show()
            }

            Thread.sleep(1000)
            runOnUiThread {
                updateStatus("📺 Streaming: Call of Duty Modern Warfare")
            }
        }.start()
    }

    private fun updateStatus(message: String) {
        runOnUiThread {
            tvStatus.text = message
            Log.d("Status", message)
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            updateStatus("📷 Camera Ready | Waiting for PS4...")
        } catch (e: Exception) {
            Log.e("CameraActivity", "Bind failed", e)
            Toast.makeText(this, "Camera start failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        if (isUSBBound) {
            unbindService(usbConnection)
        }
    }
}