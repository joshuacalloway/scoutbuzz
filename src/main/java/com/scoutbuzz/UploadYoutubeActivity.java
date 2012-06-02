package com.scoutbuzz;

import android.app.Activity;
import android.os.Bundle;
import android.content.ContentResolver;
import android.net.Uri;
import android.content.Intent;
import java.io.FileNotFoundException;
import android.content.ContentValues;
import android.provider.MediaStore.Video;
import android.provider.MediaStore;
import android.util.Log;

public class UploadYoutubeActivity extends Activity
{
  private final String TAG="scoutbuzz";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Uri fileUri = (Uri)getIntent().getExtras().get(Intent.EXTRA_STREAM);
    Log.d(TAG, "fileUri is " + fileUri);
    Log.d(TAG, "fileUri.getPath " + fileUri.getPath());
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.setType("video/3gpp");
    intent.putExtra(Intent.EXTRA_STREAM, fileUri);
    startActivity(Intent.createChooser(intent,"Upload video via:"));
  }
}
