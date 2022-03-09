package com.laixer.swabbr.presentation.recording

import android.Manifest
import android.content.Context
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.laixer.swabbr.R
import com.laixer.swabbr.extensions.*
import com.laixer.swabbr.presentation.types.CameraDirection
import com.laixer.swabbr.presentation.types.VideoRecordingState
import com.laixer.swabbr.utils.files.FileHelper.Companion.createFile
import com.laixer.swabbr.utils.media.MediaConstants
import com.laixer.swabbr.utils.media.getPreviewOutputSize
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

// TODO If we ask permissions for the first time this doesn't work
// TODO Put all the lazy statements in a single try catch method and go to error state if we fail.
// TODO Torch with repeating request
// TODO java.lang.IllegalStateException: Handler (android.os.Handler) {cbba0a6} sending message to a Handler on a dead thread (is caught though)
// TODO Enhancement: Preview, leave, re-enter: before this a preview frame is shown, after this none is. Minor detail.
// FUTURE This is currently fixed on portrait mode. One day we may want to change this.
/**
 *  Fragment for recording video using the camera2 API. Note that all
 *  methods which require permissions are wrapped in a permission check.
 *  When any permission are denied, [onPermissionsDeclined] is called.
 *
 *  This fragment only handles recording functionality. Extend this to
 *  implement custom functionality and UI other than [surface_view_record_video].
 *
 *  This design choice was deliberate, as the video recording process is
 *  rather complex in Android. Splitting this functionality allows us to
 *  keep an overview of what's happening where. Any UI elements, timed
 *  callbacks etc should be handled by extensions of this class.
 */
abstract class RecordVideoFragment : RecordVideoInnerMethods() {

    /**
     *  Used to enumerate and open camera instances. This is instantiated once
     *  independently by [lazy] (does not need other objects than the context).
     */
    private val cameraManager: CameraManager by lazy {
        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /**
     *  Thread on which all camera operations run. This is created and started immediately, once.
     */
    private val cameraThread by lazy { HandlerThread(CAMERA_THREAD_NAME).apply { start() } }

    /**
     *  [Handler] corresponding to [cameraThread]. This is created immediately, once.
     */
    private val cameraHandler by lazy { Handler(cameraThread.looper) }

    /**
     *  Setup a persistent [Surface] for the recorder so we can use it as an output target for the
     *  camera session without preparing the recorder. This is whenever we select a camera.
     */
    private var recorderSurface: Surface? = null

    /**
     *  Media recorder which saves our recording. This is set-up each time we
     *  start recording in [tryStartRecording]. The state machine is located here:
     *  https://developer.android.com/reference/android/media/MediaRecorder
     *
     *  Note that this is only released in [onDestroy].
     *
     */
    private val mediaRecorder: MediaRecorder = MediaRecorder()

    // TODO What happens when we overwrite the file after re-recording?
    /**
     *  File where our recording will be stored. This is created once.
     */
    protected val outputFile: File by lazy { createFile(requireContext(), VIDEO_BASE_NAME, VIDEO_MIME_TYPE) }

    /**
     *  Camera object which will be opened by this fragment. Created
     *  whenever we select a new camera in [tryInitializeCamera].
     */
    private var camera: CameraDevice? = null

    /**
     *  Object representing a video recording session. Created whenever
     *  we select a new camera in [tryInitializeCamera].
     */
    private var cameraCaptureSession: CameraCaptureSession? = null

    /**
     *  Requests used only for preview in the [CameraCaptureSession]. This
     *  is created whenever we select a new camera in [tryInitializeCamera].
     */
    private var previewRequest: CaptureRequest? = null

    /**
     *  Requests used for preview and recording in the [CameraCaptureSession].
     *  Note that this does more than [previewRequest]. This is created when
     *  we start recording using [tryStartRecording].
     */
    private var recordRequest: CaptureRequest? = null

    /**
     *  Enum indicating the state of this fragment. This is used to ensure
     *  we can't execute certain functionality when we shouldn't. Do not
     *  modify this. This is an observable object, which initially is set
     *  to [BEGIN_STATE].
     *
     *  With regards to thread safety and concurrency, more info here:
     *  https://kinnrot.github.io/live-data-pitfall-you-should-be-aware-of/
     */
    private var state: MutableLiveData<VideoRecordingState> = MutableLiveData(BEGIN_STATE)

    /**
     *  Gets the current [state], or [BEGIN_STATE] if the [state] is still null.
     */
    protected fun getCurrentState(): VideoRecordingState = state.value ?: BEGIN_STATE

    /**
     *  Ask for permissions right away.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askPermission(*PERMISSIONS) { /* Do nothing */ }
            .onDeclined { onPermissionsDeclined() }
    }

    /**
     *  Inflate a generic recording fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_record_video, container, false)

    /**
     *  Starts our UI and camera initialization process. This attaches the
     *  [onStateChanged] listener to [state]. This will also load the first
     *  camera when the UI is ready for it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign state listener (only when the view has been created).
        state.observe(viewLifecycleOwner, Observer { onStateChanged(it) })

        askPermission(*PERMISSIONS) {
            // Assign setup callbacks to the holder of our surface view so that the
            // camera will be setup once said element has been inflated completely.
            surface_view_record_video.holder.addCallback(object : SurfaceHolder.Callback {
                /**
                 *  Called immediately after the surface is first created.
                 */
                override fun surfaceCreated(holder: SurfaceHolder) {
                    // Get an object representing the size of our auto fit surface view,
                    // then assign the aspect ratio of said size to the surface view.
                    val previewSize: Size = getPreviewOutputSize(
                        display = surface_view_record_video.display,
                        characteristics = camera?.let { cameraManager.getCameraCharacteristics(it.id) }
                            ?: cameraManager.getFirstFrontFacingCameraCharacteristics(),
                        targetClass = SurfaceHolder::class.java
                    )

                    // Note that setting these parameters only affect the preview screen,
                    // not the output video of the media recorder. It doesn't matter if the
                    // aspect ratio isn't matching with our video source. This only matters
                    // for the recording surface itself.
                    surface_view_record_video.setAspectRatio(previewSize.width, previewSize.height)

                    // If this is not a re-enter, change our state to ready, then load our first camera.
                    if (state.value == VideoRecordingState.LOADING) {

                        // First change state
                        state.postValue(VideoRecordingState.UI_READY)

                        /**
                         *  To ensure that size is set we initialize the camera in the view's thread.
                         *  Note that if we already have a camera open we initialize it again. The
                         *  [tryInitializeCamera] expects [VideoRecordingState.UI_READY] as [state]
                         *  in this case. The only way to exit said state is by [tryInitializeCamera].
                         *  We only explicitly call [tryInitializeCamera] if our [state] is still in
                         *  [VideoRecordingState.UI_READY], else some other caller has influenced the
                         *  state (which is perfectly fine).
                         */
                        surface_view_record_video.post {
                            if (state.value == VideoRecordingState.UI_READY) {
                                tryInitializeCamera(
                                    camera?.id
                                        ?: cameraManager.getFirstCameraIdInDirection(CAMERA_BEGIN_DIRECTION)
                                )
                            }
                        }
                    }
                }

                // These are irrelevant for us, but required by the callback object interface.
                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit
                override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
            })
        }.onDeclined { onPermissionsDeclined() }
    }

    /**
     *  Override this method to listen to [state] changes.
     */
    protected open fun onStateChanged(state: VideoRecordingState) { }

    /**
     *  Called when we can't obtain the required permissions for whatever reason.
     *  Override this to specify custom behaviour. This shows a message followed
     *  by a simulated back press.
     */
    protected open fun onPermissionsDeclined() {
        showMessage("Your permissions are required to record videos")
        goBack()
    }

    /**
     *  Attempts to switch from front camera to back camera or vice versa.
     */
    protected open fun trySwitchCamera() {
        camera?.let {
            when (cameraManager.getCameraFacingInt(it.id)) {
                CameraCharacteristics.LENS_FACING_FRONT -> tryInitializeCamera(cameraManager.getFirstBackFacingCameraId())
                CameraCharacteristics.LENS_FACING_BACK -> tryInitializeCamera(cameraManager.getFirstFrontFacingCameraId())
                else -> Log.w(TAG, "Could not get a camera id to switch to")
            }
        }
    }

    /**
     *  Opens and sets up our camera object on the main thread.
     *
     *  @param cameraId The selected camera.
     */
    protected fun tryInitializeCamera(cameraId: String) {
        askPermission(*PERMISSIONS) {
            // Validate our state transition first.
            if (state.value != VideoRecordingState.UI_READY &&
                state.value != VideoRecordingState.READY &&
                state.value != VideoRecordingState.RECORDING_INTERRUPTED &&
                state.value != VideoRecordingState.DONE_RECORDING
            ) {
                Log.w(TAG, "Illegal state transition from ${state.value}, can't initialize camera")
                return@askPermission
            }

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    // Explicitly assign this value. We can do this since using postValue()
                    // wil dispatch on the main thread itself. Since we assign the state
                    // again later in this method, this assignment prevents a race condition.
                    state.value = VideoRecordingState.INITIALIZING_CAMERA

                    // Clean up any present resources.
                    tryCleanupCameraResources()

                    // Open the camera and get the info object.
                    camera = openCamera(cameraManager, cameraId)
                    val cameraInfo = cameraManager.getCameraInfo(cameraId, PREFERRED_VIDEO_SIZE)

                    // Re-create the recorder surface to match the selected size.
                    recorderSurface = createRecorderSurface(cameraInfo, outputFile)

                    /**
                     * TODO Exception
                     *  E/RecordVideoFragment: Error while trying to initialize camera
                     *  java.lang.IllegalStateException: surface_view_record_video must not be null
                     *  at com.laixer.swabbr.presentation.recording.RecordVideoFragment$tryInitializeCamera$1$1.invokeSuspend(RecordVideoFragment.kt:279)
                     *  ...
                     *
                     *  tempfix:
                     */
                    if (surface_view_record_video == null) {
                        Log.w(TAG, "Exception for camera initialization. surface view was null")
                        return@launch
                    }

                    // Target the output to our surface view and to our recorder surface.

                    val targets = listOf(surface_view_record_video.holder.surface, recorderSurface!!)

                    /**
                     *  Create a new capture sessions with a preview request which will be repeated.
                     *  This sends the capture request as frequently as possible until the session
                     *  is torn down or [CameraCaptureSession.stopRepeating] is called.
                     */
                    cameraCaptureSession = createCameraCaptureSession(camera!!, targets, cameraHandler)
                    previewRequest = createCaptureRequest(
                        session = cameraCaptureSession!!,
                        surfaces = listOf(surface_view_record_video.holder.surface)
                    )
                    cameraCaptureSession!!.setRepeatingRequest(previewRequest!!, null, cameraHandler)

                    // TODO Torch - this is how you are supposed to do it. There is also a
                    //      cameraManager.enableTorch() method, but the repeating request must
                    //      have it's flag for the torch set. Toggling torch = new request.
                    // val prb = camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    // prb.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
                    // prb.addTarget(surface_view_record_video.holder.surface)
                    // val pr = prb.build()
                    // cameraCaptureSession!!.setRepeatingRequest(pr, null, cameraHandler)

                    state.postValue(VideoRecordingState.READY)

                } catch (e: Exception) {
                    Log.e(TAG, "Error while trying to initialize camera", e)
                    state.postValue(VideoRecordingState.ERROR)
                }
            }
        }.onDeclined { onPermissionsDeclined() }
    }

    /**
     *  Attempts to start recording the video.
     */
    protected open fun tryStartRecording() {
        askPermission(*PERMISSIONS) {
            if (state.value != VideoRecordingState.READY) {
                Log.w(TAG, "Illegal state transition from ${state.value}, can't start recording")
                return@askPermission
            }

            // Validate all our resources are present.
            if (camera == null) {
                Log.e(TAG, "Camera not connected, can't start recording")
                return@askPermission
            }
            if (cameraCaptureSession == null) {
                Log.e(TAG, "Camera capture session not initialized, can't start recording")
                return@askPermission
            }
            if (recorderSurface == null) {
                Log.e(TAG, "Recorder surface not initialized, can't start recording")
                return@askPermission
            }

            try {
                // First get the camera info.
                val cameraInfo = cameraManager.getCameraInfo(camera!!.id, PREFERRED_VIDEO_SIZE)

                // New record request which outputs to the preview and recorder surface.
                recordRequest = createCaptureRequest(
                    cameraCaptureSession!!,
                    listOf(surface_view_record_video.holder.surface, recorderSurface!!),
                    cameraInfo.fps
                )

                // Start recording repeating requests, which will stop the ongoing preview
                // repeating requests without having to explicitly call `session.stopRepeating`.
                cameraCaptureSession!!.setRepeatingRequest(recordRequest!!, null, cameraHandler)

                // TODO Are we making a bunch of non-released media recorders now?
                // Create a new media recorder and start it. If we don't recreate the
                // media recorder here we can't guarantee its state.
                initializeMediaRecorder(mediaRecorder, cameraInfo, recorderSurface!!, outputFile).apply {
                    prepare()
                    start()
                }

                state.postValue(VideoRecordingState.RECORDING)

            } catch (e: Exception) {
                Log.e(TAG, "Error while trying to start recording", e)
                state.postValue(VideoRecordingState.ERROR)
            }
        }.onDeclined { onPermissionsDeclined() }
    }

    /**
     *  Attempts to stop recording the video.
     */
    protected open fun tryStopRecording() {
        if (state.value != VideoRecordingState.RECORDING) {
            Log.w(TAG, "Illegal state change from ${state.value}, can't stop recording")
            return
        }

        try {
            // Explicitly stop recording and release resources.
            mediaRecorder.stop()
            recorderSurface?.release()
            tryCleanupCameraResources()

            state.postValue(VideoRecordingState.DONE_RECORDING)
        } catch (e: Exception) {
            Log.e(TAG, "Error while trying to stop recording", e)
            state.postValue(VideoRecordingState.ERROR)
        }
    }

    /**
     *  Call this to re-setup adn thus re-enable our recording functionality.
     */
    protected open fun tryReset() {
        // First validate our state
        if (state.value != VideoRecordingState.UI_READY &&
            state.value != VideoRecordingState.READY &&
            state.value != VideoRecordingState.DONE_RECORDING &&
            state.value != VideoRecordingState.RECORDING_INTERRUPTED
        ) {
            Log.w(TAG, "Illegal state transition from ${state.value}, can't reset")
            return
        }

        // Only then reset by initializing the camera.
        tryInitializeCamera(camera?.id ?: cameraManager.getFirstCameraIdInDirection(CAMERA_BEGIN_DIRECTION))
    }

    /**
     *  Called when we are recording but get interrupted. This will
     *  clean up our current resources and abort the recording,
     *  followed by a state change.
     */
    private fun onRecordingInterrupted() {
        try {
            mediaRecorder.stop()
            recorderSurface?.release()

            state.postValue(VideoRecordingState.RECORDING_INTERRUPTED)

            // TODO Debug, remove this
            showMessage("Recording process was interrupted")

        } catch (e: Exception) {
            Log.e(TAG, "Error while calling onRecordingInterrupted()", e)
            state.postValue(VideoRecordingState.ERROR)
        }
    }


    /**
     *  Clean up the current camera resources if any are present. This
     *  should be called before [tryInitializeCamera] and in [onStop].
     */
    private fun tryCleanupCameraResources() {
        try {
            camera?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't close camera", e)
            state.postValue(VideoRecordingState.ERROR)
        }
    }

    /**
     *  When we are recording and this is paused, call [onRecordingInterrupted].
     */
    override fun onPause() {
        super.onPause()

        if (state.value == VideoRecordingState.RECORDING) {
            // This method has a T/c to always allow onPause() to execute.
            onRecordingInterrupted()
        }
    }

    /**
     *  When we resume this fragment, try to reset the functionality in some states.
     *  When we are loading, the setup process should not be interfered with. When
     *  we are done recording the reset process should be triggered explicitly.
     */
    override fun onResume() {
        super.onResume()

        if (state.value == VideoRecordingState.UI_READY ||
            state.value == VideoRecordingState.READY
        ) {
            tryReset()
        }
    }

    // TODO Can we guarantee that this is always called?
    /**
     *  Close the camera when this fragment exits.
     */
    override fun onStop() {
        super.onStop()

        tryCleanupCameraResources()
    }

    /**
     *  Clean up resources when this object gets disposed.
     */
    override fun onDestroy() {
        super.onDestroy()

        cameraThread.quitSafely()
        mediaRecorder.release()
        recorderSurface?.release()
    }

    companion object {
        private val TAG = RecordVideoFragment::class.java.simpleName
        private const val CAMERA_THREAD_NAME = "Recording video camera thread"
        private val BEGIN_STATE = VideoRecordingState.LOADING
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        // TODO Move to some config file
        // Video constants
        internal const val VIDEO_BASE_NAME = "video"
        internal const val VIDEO_MIME_TYPE = MediaConstants.VIDEO_MP4_MIME_TYPE
        internal val PREFERRED_VIDEO_SIZE = MediaConstants.SIZE_1080p
        internal val CAMERA_BEGIN_DIRECTION = CameraDirection.FRONT
    }
}
