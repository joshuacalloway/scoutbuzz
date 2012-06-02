package com.scoutbuzz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockCameraActivity extends Activity {
  private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  Intent youtubeIntent;
  Intent uploadI;
  private static String TAG="scoutbuzz";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    youtubeIntent = new Intent(this, UploadYoutubeActivity.class);

    setContentView(R.layout.stockcamera);
    Log.d(TAG, "onCreate StockCameraActivity");

    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    setResult(RESULT_OK);

    uploadI = new Intent(Intent.ACTION_SEND);
    uploadI.setType("video/3gp");
    
    uploadI.putExtra(Intent.EXTRA_TEXT, "sample text");
    uploadI.putExtra(Intent.EXTRA_TITLE, "scoutbuzz video");
    
    // Add a listener to the Capture button
    Button uploadButton = (Button) findViewById(R.id.button_upload);
    uploadButton.setOnClickListener(
				     new View.OnClickListener() {
				       @Override
				       public void onClick(View v) {
					 startActivity(Intent.createChooser(uploadI, "kerker"));
				       }}
				    );
  



   Button youtubeUploadButton = (Button) findViewById(R.id.button_youtube);
    youtubeUploadButton.setOnClickListener(
				     new View.OnClickListener() {
				       @Override
				       public void onClick(View v) {
					 startActivity(youtubeIntent);
				       }
				     });


    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
	Uri theUri = (Uri)data.getData();
	uploadI.putExtra(Intent.EXTRA_STREAM, theUri);
	Log.d(TAG, "theUri is : " + theUri);
	youtubeIntent.putExtra(Intent.EXTRA_STREAM,theUri);
	
  	Toast.makeText(this, "Video saved to:\n" +
  		       theUri.getPath(), Toast.LENGTH_LONG).show();

      } else if (resultCode == RESULT_CANCELED) {
  	// User cancelled the video capture
      } else {
  	// Video capture failed, advise user
      }
    }
  }
}
