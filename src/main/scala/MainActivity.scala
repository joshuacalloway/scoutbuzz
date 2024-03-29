package com.scoutbuzz

import _root_.android.app.Activity
import _root_.android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import java.io.File

class MainActivity extends Activity with TypedActivity {
  val TAG="scoutbuzz"


  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)
    Log.d(TAG, "onCreate ...")
    findView(TR.textview).setText("version 1.01")
    val i = new Intent(this, classOf[CustomCameraActivity])
    i.putExtra("Value1", "This value one for ActivityTwo ")
    i.putExtra("Value2", "This value two ActivityTwo")
    val captureButton = findView(TR.button_customcapture);
    captureButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          def onClick(v: View) {
            findView(TR.textview).setText("button pressed!")
            startActivity(i)
          }
        }
    )

    val stockI = new Intent(this, classOf[StockCameraActivity])
    i.putExtra("Value1", "This value one for ActivityTwo ")
    i.putExtra("Value2", "This value two ActivityTwo")

    val stockCaptureButton = findView(TR.button_stockcapture);
    stockCaptureButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          def onClick(v: View) {
            startActivity(stockI)
          }
        }
    )
  }

}
