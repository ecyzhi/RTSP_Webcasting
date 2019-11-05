package com.mercy.alpacalive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetupIP extends AppCompatActivity {

    private EditText serverIP;
    private Button setBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_ip);

        serverIP = findViewById(R.id.etServerIP);
        setBtn = findViewById(R.id.btnSet);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupIP.this,LoginActivity.class);
                intent.putExtra("SERVER_IP",serverIP.getText());
                startActivity(intent);

            }
        });
    }
}
