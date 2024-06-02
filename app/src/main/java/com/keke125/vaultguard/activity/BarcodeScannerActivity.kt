package com.keke125.vaultguard.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.keke125.vaultguard.ui.theme.VaultGuardTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerActivity : ComponentActivity() {

    private lateinit var analysisExecutor: ExecutorService

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(
                baseContext, "無法取得相機權限", Toast.LENGTH_SHORT
            ).show()
            setResult(RESULT_CANCELED, intent)
            finish()
        } else {
            setCameraPreview()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request camera permissions
        if (allPermissionsGranted()) {
            setCameraPreview()
        } else {
            requestPermissions()
        }

        analysisExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @OptIn(ExperimentalGetImage::class)
    private fun setCameraPreview() {
        setContent {
            VaultGuardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val previewView = remember {
                        PreviewView(this)
                    }

                    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE
                    ).build()
                    // getClient() creates a new instance of the MLKit barcode scanner with the specified options
                    val barcodeScanner = BarcodeScanning.getClient(options)

                    // setting up the analysis use case
                    val analysis = ImageAnalysis.Builder().build()

                    // define the actual functionality of our analysis use case
                    analysis.setAnalyzer(
                        // newSingleThreadExecutor() will let us perform analysis on a single worker thread
                        analysisExecutor
                    ) { imageProxy ->
                        //processImageProxy(scanner, imageProxy, this, setResult, analysis)
                        imageProxy.image?.let { image ->
                            val inputImage = InputImage.fromMediaImage(
                                image, imageProxy.imageInfo.rotationDegrees
                            )
                            barcodeScanner.process(inputImage).addOnSuccessListener { barcodeList ->
                                val barcode = barcodeList.getOrNull(0)
                                if (barcode != null) {
                                    val valueType = barcode.valueType
                                    // See API reference for complete list of supported types
                                    when (valueType) {
                                        Barcode.TYPE_URL -> {
                                            val url = barcode.url!!.url
                                            if (url != null) {
                                                intent.putExtra("url", url)
                                                setResult(RESULT_OK, intent)
                                                finish()
                                                analysis.clearAnalyzer()
                                            }
                                        }

                                        Barcode.TYPE_TEXT -> {
                                            val url = barcode.rawValue
                                            if (url != null) {
                                                intent.putExtra("url", url)
                                                setResult(RESULT_OK, intent)
                                                finish()
                                                analysis.clearAnalyzer()
                                            }
                                        }
                                    }
                                }
                            }.addOnCanceledListener {
                                Toast.makeText(this, "取消掃描!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    this, it.localizedMessage, Toast.LENGTH_SHORT
                                ).show()
                            }.addOnCompleteListener {
                                // When the image is from CameraX analysis use case, must
                                // call image.close() on received images when finished
                                // using them. Otherwise, new images may not be received
                                // or the camera may stall.
                                imageProxy.image?.close()
                                imageProxy.close()
                            }
                        }
                    }

                    cameraProviderFuture.addListener({
                        // Used to bind the lifecycle of cameras to the lifecycle owner
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                        // Preview
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        // Select back camera as a default
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            // Unbind use cases before rebinding
                            cameraProvider.unbindAll()

                            // Bind use cases to camera
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, analysis
                            )

                        } catch (exc: Exception) {
                            Log.e("BarcodeScanner", "Use case binding failed", exc)
                        }

                    }, ContextCompat.getMainExecutor(this))

                    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        analysisExecutor.shutdown()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).toTypedArray()
    }
}

fun getSecretFromUri(uri: Uri): String? {
    if (uri.authority != null && uri.authority == "totp") {
        if (uri.queryParameterNames.contains("secret")) {
            val secret = uri.getQueryParameter("secret")
            return secret
        } else {
            return null
        }
    } else {
        return null
    }
}
