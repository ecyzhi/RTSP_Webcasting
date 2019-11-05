package com.mercy.alpacalive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//volley need to add dependencies
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";
    private EditText usermail, userpass;
    private Button btnLogin;
    private static String URL_LOGIN = "http://192.168.0.137:8080/alpacalive/UserLogin.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usermail = findViewById(R.id.txt_email);
        userpass = findViewById(R.id.txt_pwd);
        btnLogin = findViewById(R.id.btn_login);
        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mail = usermail.getText().toString().trim();
                String pass = userpass.getText().toString().trim();

                if(!mail.isEmpty() || !pass.isEmpty()){
                    Login(mail,pass);
                }else{
                    usermail.setError("Please enter your email address");
                    userpass.setError("Please enter password");
                }
            }
        });

    }


    public void goToEventList (View view){
        Intent intent = new Intent(this, EventListing.class);
        startActivity(intent);
    }

    private void Login(final String userEmail, final String userPassword){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //to retrieve error return from JSon
                        //usermail.setText(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
//                            Validate id or password
                            String message = jsonObject.getString("message");

                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){

                                for(int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    // To display the toast which says the status of login
                                    String userID = object.getString("userID").trim();
                                    String userName = object.getString("userName").trim();
                                    String userEmail = object.getString("userEmail").trim();


                                    SharedPreferences.Editor preferencesEditor = sharedPref.edit();
                                    preferencesEditor.putString("USER_ID",userID);
                                    preferencesEditor.putString("USER_NAME",userName);
                                    preferencesEditor.putString("USER_EMAIL",userEmail);

                                    preferencesEditor.apply();

                                    Intent intent = new Intent(LoginActivity.this, EventListing.class);
                                    startActivity(intent);
                                }
                            } else {
                                if(message.equals("wrongpass")) {
                                    Toast.makeText(LoginActivity.this, "Failed to login. Incorrect password.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "ID does not exist.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,
                                    "JSON Error " +e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,
                                "Volley Error " +error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userEmail",userEmail);
                params.put("userPassword",userPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
