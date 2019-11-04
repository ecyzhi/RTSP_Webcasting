package com.pedro.rtpstreamer;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pedro.rtpstreamer.defaultexample.ExampleRtspActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.security.SecureRandom;

public class LiveListing extends AppCompatActivity {

    private String serverIP = "192.168.0.137";

    private String roomCode = "";
    private String roomUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_listing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                roomCode = randomString(10);
                roomUrl = "rtsp://" + serverIP + "/alpacalive/" + roomCode;

                Intent intent = new Intent(LiveListing.this, ExampleRtspActivity.class);
                intent.putExtra("ROOM_CODE_KEY", roomCode);
                intent.putExtra("ROOM_URL_KEY", roomUrl);
                startActivity(intent);
            }
        });
    }

    public String randomString(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

}
