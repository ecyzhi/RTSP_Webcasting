package com.mercy.alpacalive.rtspplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mercy.alpacalive.R;
import com.mercy.vlc.VlcListener;
import com.mercy.vlc.VlcVideoLibrary;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pedro on 25/06/17.
 */
public class RtspPlayer extends AppCompatActivity implements VlcListener, View.OnClickListener {

    private VlcVideoLibrary vlcVideoLibrary;
    private Button bStartStop;
    private EditText etEndpoint;

    private String[] options = new String[]{":fullscreen"};

    List<String> dbRoomCodeList = new ArrayList<>();
    List<String> roomCodeArray;
    private int position = -1;
    private Button btnPrevious;
    private Button btnNext;
    private ProgressBar progressBar;
    private String currentRoomCode;

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";

    String strRoomCodeList = "";
    String roomUrl = "";

    public int mHeight;
    public int mWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rtsp_player);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);

        // Getting mobile display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;

        bStartStop = findViewById(R.id.b_start_stop);

        bStartStop.setOnClickListener(this);
        etEndpoint = findViewById(R.id.et_endpoint);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        final String serverIP = sharedPref.getString("SERVER_IP","");


        // Split the room code string into list
        strRoomCodeList = getIntent().getExtras().getString("ROOM_CODE_LIST_KEY","");
        String[] items = strRoomCodeList.split("\\s*,\\s*");
        roomCodeArray = Arrays.asList(items);

        for(int i = 0 ; i < roomCodeArray.size(); i++){
            if(roomCodeArray.get(i) != null)
                dbRoomCodeList.add(roomCodeArray.get(i));
        }

        checkPosition();


        // Entering generated rtsp url automatically
        // If roomCode != 10 then it is raspberry pi server
        roomUrl = getIntent().getExtras().getString("ROOM_URL_KEY","Error");
        currentRoomCode = getIntent().getExtras().getString("ROOM_CODE_KEY","Error");

        if(currentRoomCode.length() == 10) {
            etEndpoint.setText(roomUrl);
        }
        else{
            etEndpoint.setText(currentRoomCode);
        }




        // Onclick listener for previous and next button
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((position - 1) >= 0){
                    // Stop the video before changing source
                    RtspPlayer.this.onClick(btnPrevious);

//                    etEndpoint.setText("rtsp://" + serverIP + "/alpacalive/" + dbRoomCodeList.get(position-1));
//                    position--;

                    roomUrl = "rtsp://" + serverIP + "/alpacalive/" + dbRoomCodeList.get(position-1);
                    Intent intent = new Intent(RtspPlayer.this, RtspPlayer.class);
                    intent.putExtra("ROOM_URL_KEY", roomUrl);
                    intent.putExtra("EVENT_ID", getIntent().getExtras().getString("EVENT_ID","Error"));
                    intent.putExtra("ROOM_CODE_LIST_KEY", strRoomCodeList);
                    intent.putExtra("ROOM_CODE_KEY", dbRoomCodeList.get(position-1));
                    startActivity(intent);

                    overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "This is the first room", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((position + 1) < dbRoomCodeList.size()){
                    // Stop the video before changing source
                    RtspPlayer.this.onClick(btnNext);

//                    etEndpoint.setText("rtsp://" + serverIP + "/alpacalive/" + dbRoomCodeList.get(position+1));
//                    position++;

                    roomUrl = "rtsp://" + serverIP + "/alpacalive/" + dbRoomCodeList.get(position+1);
                    Intent intent = new Intent(RtspPlayer.this, RtspPlayer.class);
                    intent.putExtra("ROOM_URL_KEY", roomUrl);
                    intent.putExtra("EVENT_ID", getIntent().getExtras().getString("EVENT_ID","Error"));
                    intent.putExtra("ROOM_CODE_LIST_KEY", strRoomCodeList);
                    intent.putExtra("ROOM_CODE_KEY", dbRoomCodeList.get(position+1));
                    startActivity(intent);

                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "This is the last room", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Start the player automatically once this activity start
        // Error: The video are misaligned if the width and height are not set, the VLCVideoLibrary.java is modified at the setMedia()
        vlcVideoLibrary.setWidth(mWidth);
        vlcVideoLibrary.setHeight(mHeight);
        bStartStop.performClick();
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, nobody streaming from this room!", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
        bStartStop.setText(getString(R.string.start_player));
    }

    // Set Progress Bar
    @Override
    public void onBuffering(MediaPlayer.Event event) {
        progressBar=findViewById(R.id.pgBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (!vlcVideoLibrary.isPlaying()) {
            vlcVideoLibrary.play(etEndpoint.getText().toString());
            bStartStop.setText(getString(R.string.stop_player));
        } else {
            vlcVideoLibrary.stop();
            bStartStop.setText(getString(R.string.start_player));
        }
    }

    private void checkPosition(){
        for(int i = 0 ; i < dbRoomCodeList.size(); i++){
            if(dbRoomCodeList.get(i).equals(getIntent().getExtras().getString("ROOM_CODE_KEY","Error"))){
                position = i;
            }
        }
    }

    // TODO: Stop the player when use press back button on phone
    // Error: Call vlc stop function will cause the activity crashes
    @Override
    protected void onStop() {
        super.onStop();
    }
}
