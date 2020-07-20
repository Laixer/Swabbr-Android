package io.antmedia.android.broadcaster;

import kotlinx.coroutines.Job

/**
 * Created by mekya on 29/03/2017.
 */
interface ILiveVideoBroadcaster {

    fun initializeCamera()

    fun release();

    /**
     * Changes the camera,
     * if active camera is back camera, releases the back camera and
     * open the front camera, it behaves same with the front camera
     */
    fun changeCamera();

    fun canChangeCamera(): Boolean

    fun toggleTorch()

    fun canToggleTorch(): Boolean

    /**
     *
     * @param rtmpUrl the rtmp url which should be in form rtmp://SERVER_ADDRESS/APP_NAME/STREAM_NAME/STREAM_KEY
     * @throws java.net.ConnectException if unable to connect
     */
    fun connect(rtmpUrl: String);

    /**
     * Set adaptive streaming enable or disable
     *
     * @param enable, if true , adaptive streaming is enabled, defaults false
     */
    fun setAdaptiveStreaming(enable: Boolean);

    /**
     * Pauses and releases the camera, it is safe to call this function in OnPause of the activity
     */
    fun pause();

    /**
     *
     * @return true if broadcasting is active and app is connected to server
     * false if it is not connected or connection is dropped
     */
    fun isConnected(): Boolean;

    /**
     * Starts broadcasting, can only be called after connect()
     */
    fun startBroadcasting(): Job

    /**
     * Stops broadcastings to the server, can only be called after startBroadcasting()
     */
    fun stopBroadcasting(): Job
}
