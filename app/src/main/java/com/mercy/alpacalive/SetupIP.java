package com.mercy.alpacalive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SetupIP extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";

    private EditText serverIP;
    private Button setBtn;

    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_ip);

        serverIP = findViewById(R.id.etServerIP);
        setBtn = findViewById(R.id.btnSet);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(SetupIP.this, PERMISSIONS)) {
                    Intent intent = new Intent(SetupIP.this, LoginActivity.class);

                    String strServerIP = serverIP.getText().toString();

                    sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditor = sharedPref.edit();
                    preferencesEditor.putString("SERVER_IP", strServerIP);
                    preferencesEditor.apply();

//                intent.putExtra("SERVER_IP",serverIP.getText());
                    startActivity(intent);
                }else {
                    showPermissionsErrorAndRequest();
                }

            }
        });

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    private void showPermissionsErrorAndRequest() {
        Toast.makeText(this, "You need permissions before", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
