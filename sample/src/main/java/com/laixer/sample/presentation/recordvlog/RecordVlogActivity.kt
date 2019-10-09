package com.laixer.sample.presentation.recordvlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.graphics.Matrix
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.laixer.sample.R
import java.io.File
import java.util.concurrent.TimeUnit

// This is an arbitrary number used to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts
private const val REQUEST_CODE_PERMISSIONS = 10
private const val RECORD_COUNTDOWN = 3
private const val ROTATION_0 = 0
private const val ROTATION_90 = 90
private const val ROTATION_180 = 180
private const val ROTATION_270 = 270
private const val COUNTDOWN_MILLISECONDS = 3000.toLong()
private const val COUNTDOWN_INTERVAL_MILLISECONDS = 1000.toLong()
private const val MINIMUM_RECORD_TIME = 800
private const val PROGRESSBAR_INTERVAL_MILLISECONDS = 10.toLong()

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

class RecordVlogActivity : AppCompatActivity(), LifecycleOwner {

    private var progressBar: ProgressBar? = null
    private var countdown = RECORD_COUNTDOWN
    private var progressStatus = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordvlog)
        viewFinder = findViewById(R.id.view_finder)

        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

        startCountdown()
    }

    private lateinit var viewFinder: TextureView

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        // Get resolution info
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)

        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(screenSize)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // Update the SurfaceTexture
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setTargetAspectRatio(Rational(1, 1))
                // Select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Image capture use case and attached button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        val directory = Environment.getExternalStoragePublicDirectory("/testlocation")
        findViewById<ImageButton>(R.id.capture_button).setOnClickListener {
            val file = File(
                directory,
                "${System.currentTimeMillis()}.jpg"
            )
            imageCapture.takePicture(file,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(error: ImageCapture.UseCaseError, message: String, exc: Throwable?) {
                        val msg = "Photo capture failed: $message"
                        Toast.makeText(baseContext, "$msg === DIRECTORY: $directory", Toast.LENGTH_LONG).show()
                        Log.e("CameraXApp", msg)
                        exc?.printStackTrace()
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                        Log.d("CameraXApp", msg)
                    }
                }
            )
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> ROTATION_0
            Surface.ROTATION_90 -> ROTATION_90
            Surface.ROTATION_180 -> ROTATION_180
            Surface.ROTATION_270 -> ROTATION_270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun startCountdown() {
        val countdownText: TextView = findViewById(R.id.countdown)
        countdownText.text = countdown.toString()
        countdownText!!.visibility = View.VISIBLE

        val timer = object : CountDownTimer(COUNTDOWN_MILLISECONDS, COUNTDOWN_INTERVAL_MILLISECONDS) {
            override fun onTick(millisUntilFinished: Long) {
                countdown--
                countdownText.text = countdown.toString()
            }

            override fun onFinish() {
                countdownText!!.visibility = View.INVISIBLE
                startProgressBar()
            }
        }
        timer.start()
    }

    private fun startProgressBar() {
        progressBar = findViewById(R.id.progress_bar)
        progressBar!!.visibility = View.VISIBLE
        // Start long running operation in a background thread
        Thread(Runnable {
            while (progressStatus <= MINIMUM_RECORD_TIME) {
                progressStatus += 1
                // Update the progress bar to display the current value in the text view
                handler.post {
                    progressBar!!.progress = progressStatus
                }
                try {
                    // Sleep for 10 milliseconds.
                    TimeUnit.MILLISECONDS.sleep(PROGRESSBAR_INTERVAL_MILLISECONDS)
                    if (progressStatus == MINIMUM_RECORD_TIME) progressBar!!.visibility = View.INVISIBLE
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }
}
