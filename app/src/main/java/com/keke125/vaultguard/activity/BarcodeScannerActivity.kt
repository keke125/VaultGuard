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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    @kotlin.OptIn(ExperimentalMaterial3Api::class)
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

                    val options =
                        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
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
                    Scaffold(topBar = {
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ), navigationIcon = {
                            IconButton(onClick = {
                                finish()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回上一頁")
                            }
                        }, title = { Text("掃描QR Code") })
                    }) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AndroidView(factory = { previewView },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.8f)
                                    .drawWithContent {
                                        drawContent()
                                        drawQrBorderCanvas(
                                            curve = 32.dp,
                                            strokeWidth = 3.dp,
                                            capSize = 62.dp,
                                            gapAngle = 30,
                                        )
                                    })
                            Column {
                                Text(
                                    text = "自動掃描中...",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    text = "Vault Guard只會取得圖片中的TOTP驗證碼，\n不會儲存任何圖片。",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        analysisExecutor.shutdown()
    }

    private fun DrawScope.drawQrBorderCanvas(
        borderColor: Color = Color.White,
        curve: Dp,
        strokeWidth: Dp,
        capSize: Dp,
        gapAngle: Int = 20,
        shadowSize: Dp = strokeWidth * 2,
        cap: StrokeCap = StrokeCap.Square,
        lineCap: StrokeCap = StrokeCap.Round,
    ) {
        val curvePx = curve.toPx()
        val mCapSize = capSize.toPx()
        val sideLength = minOf(size.width, size.height) * 0.6f // Reduce the size to 60%
        val offsetX = (size.width - sideLength) / 2
        val offsetY = (size.height - sideLength) / 2
        val sweepAngle = 90 / 2 - gapAngle / 2f

        strokeWidth.toPx().toInt()
        for (i in 4..shadowSize.toPx().toInt() step 2) {
            drawRoundRect(
                color = Color(0x05000000),
                size = Size(sideLength, sideLength),
                topLeft = Offset(offsetX, offsetY),
                style = Stroke(width = i * 1f),
                cornerRadius = CornerRadius(
                    x = curvePx, y = curvePx
                ),
            )
        }

        val mCurve = curvePx * 2

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 0f,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX + sideLength - mCurve, offsetY + sideLength - mCurve
            )
        )
        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 90 - sweepAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX + sideLength - mCurve, offsetY + sideLength - mCurve
            )
        )

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX, offsetY + sideLength - mCurve
            )
        )
        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 180 - sweepAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX, offsetY + sideLength - mCurve
            )
        )

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 180f,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX, offsetY
            )
        )

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 270 - sweepAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX, offsetY
            )
        )

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 270f,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX + sideLength - mCurve, offsetY
            )
        )

        drawArc(
            color = borderColor,
            style = Stroke(strokeWidth.toPx(), cap = cap),
            startAngle = 360 - sweepAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            size = Size(mCurve, mCurve),
            topLeft = Offset(
                offsetX + sideLength - mCurve, offsetY
            )
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + sideLength, offsetY + sideLength - mCapSize),
            Offset(offsetX + sideLength, offsetY + sideLength - curvePx),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + sideLength - mCapSize, offsetY + sideLength),
            Offset(offsetX + sideLength - curvePx, offsetY + sideLength),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + mCapSize, offsetY + sideLength),
            Offset(offsetX + curvePx, offsetY + sideLength),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX, offsetY + sideLength - curvePx),
            Offset(offsetX, offsetY + sideLength - mCapSize),
            strokeWidth.toPx(),
            lineCap
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX, offsetY + curvePx),
            Offset(offsetX, offsetY + mCapSize),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + curvePx, offsetY),
            Offset(offsetX + mCapSize, offsetY),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + sideLength - curvePx, offsetY),
            Offset(offsetX + sideLength - mCapSize, offsetY),
            strokeWidth.toPx(),
            lineCap,
        )

        drawLine(
            SolidColor(borderColor),
            Offset(offsetX + sideLength, offsetY + curvePx),
            Offset(offsetX + sideLength, offsetY + mCapSize),
            strokeWidth.toPx(),
            lineCap
        )
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