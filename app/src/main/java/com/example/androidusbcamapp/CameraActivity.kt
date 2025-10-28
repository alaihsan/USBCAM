package com.example.androidusbcamapp

import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.serenegiant.common.BaseActivity
import com.serenegiant.usb.CameraDialog
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.UVCCamera
import com.serenegiant.widget.UVCCameraTextureView

class CameraActivity : BaseActivity(), CameraDialog.CameraDialogParent {

    private lateinit var usbMonitor: USBMonitor
    private var uvcCamera: UVCCamera? = null
    private lateinit var cameraView: UVCCameraTextureView

    private val uvcConnection = object : USBMonitor.OnDeviceConnectListener {
        override fun onAttach(device: android.hardware.usb.UsbDevice?) {
            Toast.makeText(this@CameraActivity, "USB device attached", Toast.LENGTH_SHORT).show()
            requestPermission(device)
        }

        override fun onConnect(device: android.hardware.usb.UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?, createNew: Boolean) {
            releaseCamera()
            val camera = UVCCamera()
            try {
                camera.open(ctrlBlock)
                camera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.FRAME_FORMAT_MJPEG)
                uvcCamera = camera
                camera.setPreviewTexture(cameraView.surfaceTexture)
                camera.startPreview()
            } catch (e: Exception) {
                Log.e("CameraActivity", "Failed to start camera", e)
            }
        }

        override fun onDisconnect(device: android.hardware.usb.UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
            releaseCamera()
        }

        override fun onDettach(device: android.hardware.usb.UsbDevice?) {
            Toast.makeText(this@CameraActivity, "USB device detached", Toast.LENGTH_SHORT).show()
            releaseCamera()
        }

        override fun onCancel(device: android.hardware.usb.UsbDevice?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraView = findViewById(R.id.camera_view)
        cameraView.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT)

        usbMonitor = USBMonitor(this, uvcConnection)
    }

    override fun onStart() {
        super.onStart()
        usbMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        usbMonitor.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
        usbMonitor.destroy()
    }

    private fun requestPermission(device: android.hardware.usb.UsbDevice?) {
        if (device != null) {
            usbMonitor.requestPermission(device)
        }
    }

    private fun releaseCamera() {
        uvcCamera?.close()
        uvcCamera?.destroy()
        uvcCamera = null
    }

    override fun getUSBMonitor(): USBMonitor = usbMonitor

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
