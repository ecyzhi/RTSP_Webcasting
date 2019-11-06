package com.mercy.alpacalive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText name, email, password;
    Button signUpBtn;

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";
    private String GET_URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String serverIP = sharedPref.getString("SERVER_IP", "");
        GET_URL = "http://" + serverIP + ":8080/alpacalive/InsertUser.php";


        signUpBtn = findViewById(R.id.btnSignUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = findViewById(R.id.etUserName);
                email = findViewById(R.id.etUserEmail);
                password = findViewById(R.id.etPwd);

                String username = name.getText().toString();
                String usermail = email.getText().toString();
                String pwd = password.getText().toString();

                signUp(username, usermail, pwd);
                finish();

            }
        });
    }

    private void signUp(final String username, final String usermail, final String pwd) {
        StringRequest strReq = new StringRequest(Request.Method.POST, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUp.this,
                            "JSON Error " + response, Toast.LENGTH_SHORT).show();
                }

//                    Toast.makeText(SignUp.this, "Added successfully", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                Toast.makeText(SignUp.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", username);
                params.put("userEmail", usermail);
                params.put("userPassword", pwd);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(SignUp.this);
        requestQueue.add(strReq);

    }

}
