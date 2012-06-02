package com.scoutbuzz;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomCameraActivity extends Activity {
  public static  int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  public static final String TAG="scoutbuzz";
  private Camera mCamera;
  private SurfaceView mPreview;
  private MediaRecorder mMediaRecorder;
  private boolean isRecording = false;


  /** A safe way to get an instance of the Camera object. */
  public static Camera getCameraInstance(){
    Camera c = null;
    try {
      c = Camera.open(); // attempt to get a Camera instance
    }
    catch (Exception e){
      // Camera is not available (in use or does not exist)
    }
    return c; // returns null if camera is unavailable
  }
  private boolean prepareVideoRecorder(){

    
    mMediaRecorder = new MediaRecorder();

    // Step 1: Unlock and set camera to MediaRecorder
    mCamera.unlock();
    mMediaRecorder.setCamera(mCamera);

    // Step 2: Set sources
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

    // Step 4: Set output file
    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

    // Step 5: Set the preview output
    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

    // Step 6: Prepare configured MediaRecorder
    try {
      mMediaRecorder.prepare();
    } catch (IllegalStateException e) {
      Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
      releaseMediaRecorder();
      return false;
    } catch (IOException e) {
      Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
      releaseMediaRecorder();
      return false;
    }
    return true;
  }

  private void setCaptureButtonText(String text) {
    Button v = (Button)findViewById(R.id.button_capture);
    v.setText(text);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.customcamera);

    Log.i(TAG, "CAUGHT EXPLICIT INTENT");

    mCamera = getCameraInstance();
    // Create our Preview view and set it as the content of our activity.
    mPreview = new CustomCameraPreview(this, mCamera);
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    preview.addView(mPreview);




    // Add a listener to the Capture button
    Button captureButton = (Button) findViewById(R.id.button_capture);
    captureButton.setOnClickListener(
				     new View.OnClickListener() {
				       @Override
				       public void onClick(View v) {
					 if (isRecording) {
					   // stop recording and release camera
					   mMediaRecorder.stop();  // stop the recording
					   releaseMediaRecorder(); // release the MediaRecorder object
					   mCamera.lock();         // take camera access back from MediaRecorder

					   // inform the user that recording has stopped
					   setCaptureButtonText("Capture");
					   isRecording = false;
					 } else {
					   // initialize video camera
					   if (prepareVideoRecorder()) {
					     // Camera is available and unlocked, MediaRecorder is prepared,
					     // now you can start recording
					     mMediaRecorder.start();

					     // inform the user that recording has started
					     setCaptureButtonText("Stop");
					     isRecording = true;
					   } else {
					     // prepare didn't work, release the camera
					     releaseMediaRecorder();
					     // inform user
					   }
					 }
				       }
				     }
				     );
  }

  /** Create a file Uri for saving an image or video */
  private static Uri getOutputMediaFileUri(int type){
    return Uri.fromFile(getOutputMediaFile(type));
  }

  /** Create a File for saving an image or video */
  private static File getOutputMediaFile(int type){
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
										  Environment.DIRECTORY_PICTURES), "MyCameraApp");
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (! mediaStorageDir.exists()){
      if (! mediaStorageDir.mkdirs()){
	Log.d("MyCameraApp", "failed to create directory");
	return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE){
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			   "IMG_"+ timeStamp + ".jpg");
    } else if(type == MEDIA_TYPE_VIDEO) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			   "VID_"+ timeStamp + ".mp4");
    } else {
      return null;
    }

    return mediaFile;
  }
   
  @Override
  protected void onPause() {
    super.onPause();
    releaseMediaRecorder();       // if you are using MediaRecorder, release it first
    releaseCamera();              // release the camera immediately on pause event
  }

  private void releaseMediaRecorder(){
    if (mMediaRecorder != null) {
      mMediaRecorder.reset();   // clear recorder configuration
      mMediaRecorder.release(); // release the recorder object
      mMediaRecorder = null;
      mCamera.lock();           // lock camera for later use
    }
  }

  private void releaseCamera(){
    if (mCamera != null){
      mCamera.release();        // release the camera for other applications
      mCamera = null;
    }
  }
}

