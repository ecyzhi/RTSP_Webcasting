package com.mercy.alpacalive.rtspplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rtsp_player);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);

        bStartStop = findViewById(R.id.b_start_stop);

        bStartStop.setOnClickListener(this);
        etEndpoint = findViewById(R.id.et_endpoint);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        final String serverIP = sharedPref.getString("SERVER_IP","");




        // Split the room code string into list
        String strRoomCodeList = getIntent().getExtras().getString("ROOM_CODE_LIST_KEY","");
        String[] items = strRoomCodeList.split("\\s*,\\s*");
        roomCodeArray = Arrays.asList(items);

        for(int i = 0 ; i < roomCodeArray.size(); i++){
            if(roomCodeArray.get(i) != null)
                dbRoomCodeList.add(roomCodeArray.get(i));
        }

        checkPosition();

        etEndpoint.setText(""+position);




        // Entering generated rtsp url automatically
        String roomUrl = getIntent().getExtras().getString("ROOM_URL_KEY","Error");
//        etEndpoint.setText(roomUrl);


//        Test for string room code list
//        etEndpoint.setText(dbRoomCodeList.size());
//        etEndpoint.setText(getIntent().getExtras().getString("ROOM_CODE_LIST_KEY",""));
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

    @Override
    public void onBuffering(MediaPlayer.Event event) {

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

    // TODO: Change the room url when detecting swipe gesture

    private void checkPosition(){
        for(int i = 0 ; i < dbRoomCodeList.size(); i++){
            if(dbRoomCodeList.get(i).equals(getIntent().getExtras().getString("ROOM_CODE_KEY","Error"))){
                position = i;
            }
        }
    }
}
