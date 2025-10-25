package com.example.androidusbcamapp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.IBinder
import android.util.Log

class USBCameraService : Service() {
    private lateinit var usbManager: UsbManager
    private var isVirtualDeviceConnected = false

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    Log.d("USBCameraService", "Virtual USB device connected")
                    simulatePS4Connection()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    Log.d("USBCameraService", "Virtual USB device disconnected")
                    isVirtualDeviceConnected = false
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        setupUsbReceiver()
        simulateVirtualUSBBus()
    }

    private fun setupUsbReceiver() {
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(usbReceiver, filter)
    }

    // ✅ SIMULASI USB DEVICE CONNECTION
    private fun simulateVirtualUSBBus() {
        Log.d("USBCameraService", "Scanning virtual USB bus...")

        // Simulate finding virtual devices
        Thread {
            Thread.sleep(2000) // Delay 2 detik

            // Virtual PS4 Capture Card
            simulateVirtualDevice(
                "Virtual PS4 Capture Card",
                1356,  // Sony Vendor ID
                1000   // Fake Product ID
            )
        }.start()
    }

    private fun simulateVirtualDevice(deviceName: String, vendorId: Int, productId: Int) {
        Log.i("USBCameraService", "🔌 VIRTUAL USB DEVICE FOUND: $deviceName")
        Log.i("USBCameraService", "   Vendor ID: $vendorId, Product ID: $productId")

        // Simulate connection process
        Thread {
            Thread.sleep(1000)
            Log.i("USBCameraService", "📡 Negotiating virtual connection...")

            Thread.sleep(1000)
            Log.i("USBCameraService", "✅ Virtual USB device ready!")
            Log.i("USBCameraService", "🎮 PS4 Video Stream: 1920x1080 @ 60fps")

            isVirtualDeviceConnected = true
            startVirtualVideoStream()
        }.start()
    }

    fun simulatePS4Connection() {
        Log.w("USBCameraService", "🎯 VIRTUAL PS4 CONNECTED!")
        Log.w("USBCameraService", "📺 Game: Call of Duty Modern Warfare")
        Log.w("USBCameraService", "🎯 Resolution: 1920x1080 | Aspect: 16:9")
    }

    // ✅ SIMULASI VIDEO STREAM PROCESSING
    private fun startVirtualVideoStream() {
        Thread {
            var frameCount = 0
            while (isVirtualDeviceConnected) {
                Thread.sleep(16) // ~60fps
                frameCount++

                if (frameCount % 60 == 0) { // Log setiap 1 detik
                    Log.i("USBCameraService", "🎥 Virtual Frame: $frameCount | 1920x1080")
                }

                // Simulate frame processing untuk fullscreen
                processVirtualFrameForFullscreen()
            }
        }.start()
    }

    // ✅ SIMULASI FULLSCREEN PROCESSING
    private fun processVirtualFrameForFullscreen() {
        // Simulasi algoritma untuk hilangkan black bars
        val sourceAspect = 16.0 / 9.0  // PS4 aspect ratio
        val targetAspect = getVirtualScreenAspect()

        when {
            targetAspect > sourceAspect -> {
                // Log.d("USBCameraService", "Applying horizontal crop for fullscreen")
            }
            targetAspect < sourceAspect -> {
                // Log.d("USBCameraService", "Applying vertical crop for fullscreen")
            }
            else -> {
                // Log.d("USBCameraService", "Aspect ratio perfect - no crop needed")
            }
        }
    }

    private fun getVirtualScreenAspect(): Double {
        // Simulate different device aspect ratios
        val aspects = listOf(16.0/9.0, 18.0/9.0, 19.5/9.0, 4.0/3.0)
        return aspects.random()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            // Receiver not registered, ignore
        }
        isVirtualDeviceConnected = false
    }
}