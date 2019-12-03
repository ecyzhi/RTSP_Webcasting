package com.mercy.alpacalive.defaultexample;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mercy.encoder.input.video.CameraOpenException;
import com.mercy.rtplibrary.base.Camera1Base;
import com.mercy.rtplibrary.rtsp.RtspCamera1;
import com.mercy.rtsp.utils.ConnectCheckerRtsp;
import com.mercy.alpacalive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * More documentation see:
 * {@link Camera1Base}
 * {@link RtspCamera1}
 */
public class ExampleRtspActivity extends AppCompatActivity
    implements ConnectCheckerRtsp, View.OnClickListener, SurfaceHolder.Callback {

  private RtspCamera1 rtspCamera1;
  private Button button;
  private Button bRecord;
  private EditText etUrl;

  private String DELETE_LIVE_URL = "";
  private SharedPreferences sharedPref;
  private String sharedPrefFile = "com.mercy.alpacalive";

  private String currentDateAndTime = "";
  private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
      + "/rtmp-rtsp-stream-client-java");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_example);
    SurfaceView surfaceView = findViewById(R.id.surfaceView);
    button = findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    bRecord = findViewById(R.id.b_record);
    bRecord.setOnClickListener(this);
    Button switchCamera = findViewById(R.id.switch_camera);
    switchCamera.setOnClickListener(this);
    etUrl = findViewById(R.id.et_rtp_url);
//    etUrl.setHint(R.string.hint_rtsp);
    rtspCamera1 = new RtspCamera1(surfaceView, this);
    rtspCamera1.setReTries(10);
    surfaceView.getHolder().addCallback(this);


    sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
    final String serverIP = sharedPref.getString("SERVER_IP","");
    DELETE_LIVE_URL = "http://" + serverIP + ":8080/alpacalive/DeleteLive.php";

    // Entering generated rtsp url automatically
    String roomUrl = getIntent().getExtras().getString("ROOM_URL_KEY","Error");
    etUrl.setText(roomUrl);
  }

  @Override
  public void onConnectionSuccessRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ExampleRtspActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtsp(final String reason) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (rtspCamera1.shouldRetry(reason)) {
          Toast.makeText(ExampleRtspActivity.this, "Retry", Toast.LENGTH_SHORT)
              .show();
          rtspCamera1.reTry(5000);  //Wait 5s and retry connect stream
        } else {
          Toast.makeText(ExampleRtspActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
              .show();
          rtspCamera1.stopStream();
          button.setText(R.string.start_button);
        }
      }
    });
  }

  @Override
  public void onNewBitrateRtsp(final long bitrate) {

  }

  @Override
  public void onDisconnectRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ExampleRtspActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthErrorRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ExampleRtspActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
        rtspCamera1.stopStream();
        button.setText(R.string.start_button);
      }
    });
  }

  @Override
  public void onAuthSuccessRtsp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(ExampleRtspActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.b_start_stop:
        if (!rtspCamera1.isStreaming()) {
          if (rtspCamera1.isRecording()
              || rtspCamera1.prepareAudio() && rtspCamera1.prepareVideo()) {
            button.setText(R.string.stop_button);
            rtspCamera1.startStream(etUrl.getText().toString());
          } else {
            Toast.makeText(this, "Error preparing stream, This device cant do it",
                Toast.LENGTH_SHORT).show();
          }
        } else {
          button.setText(R.string.start_button);
          rtspCamera1.stopStream();

          deleteRoom();
          finish();

        }
        break;
      case R.id.switch_camera:
        try {
          rtspCamera1.switchCamera();
        } catch (CameraOpenException e) {
          Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        break;
      case R.id.b_record:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          if (!rtspCamera1.isRecording()) {
            try {
              if (!folder.exists()) {
                folder.mkdir();
              }
              SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
              currentDateAndTime = sdf.format(new Date());
              if (!rtspCamera1.isStreaming()) {
                if (rtspCamera1.prepareAudio() && rtspCamera1.prepareVideo()) {
                  rtspCamera1.startRecord(
                      folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                  bRecord.setText(R.string.stop_record);
                  Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(this, "Error preparing stream, This device cant do it",
                      Toast.LENGTH_SHORT).show();
                }
              } else {
                rtspCamera1.startRecord(
                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                bRecord.setText(R.string.stop_record);
                Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
              }
            } catch (IOException e) {
              rtspCamera1.stopRecord();
              bRecord.setText(R.string.start_record);
              Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          } else {
            rtspCamera1.stopRecord();
            bRecord.setText(R.string.start_record);
            Toast.makeText(this,
                "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                Toast.LENGTH_SHORT).show();
          }
        } else {
          Toast.makeText(this, "You need min JELLY_BEAN_MR2(API 18) for do it...",
              Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }
  }
  

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {

  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    rtspCamera1.startPreview();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && rtspCamera1.isRecording()) {
      rtspCamera1.stopRecord();
      bRecord.setText(R.string.start_record);
      Toast.makeText(this,
          "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
          Toast.LENGTH_SHORT).show();
      currentDateAndTime = "";
    }
    if (rtspCamera1.isStreaming()) {
      rtspCamera1.stopStream();
      button.setText(getResources().getString(R.string.start_button));
    }
    rtspCamera1.stopPreview();
  }



  // Delete row after streamer stop streaming
  private void deleteRoom(){
    final String roomCode = getIntent().getExtras().getString("ROOM_CODE_KEY","Error");

    StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_LIVE_URL, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        try {
          JSONObject jsonObject = new JSONObject(response);
          String success = jsonObject.getString("success");

          if(success.equals("1")){
//            Toast.makeText(ExampleRtspActivity.this, "Room Deleted",Toast.LENGTH_SHORT).show();
          }
        } catch (JSONException e) {
          e.printStackTrace();
          Toast.makeText(ExampleRtspActivity.this,"JSONError" + e.toString(), Toast.LENGTH_SHORT).show();
        }

      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(ExampleRtspActivity.this,"VolleyError" + error.toString(), Toast.LENGTH_SHORT).show();
      }
    })
    {
      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("roomCode",roomCode) ;
        return params;
      }
    };

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
  }
}