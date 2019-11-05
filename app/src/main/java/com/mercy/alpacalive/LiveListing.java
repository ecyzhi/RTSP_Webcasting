package com.mercy.alpacalive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mercy.alpacalive.adapter.EventList;
import com.mercy.alpacalive.adapter.EventListAdapter;
import com.mercy.alpacalive.adapter.LiveList;
import com.mercy.alpacalive.adapter.LiveListAdapter;
import com.mercy.alpacalive.defaultexample.ExampleRtspActivity;
import com.mercy.alpacalive.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class LiveListing extends AppCompatActivity {

    private SharedPreferences sharedPref;
    public static final String TAG = "com.mercy.alpacalive";
    private String sharedPrefFile = "com.mercy.alpacalive";
    private static String GET_URL = "http://192.168.0.137:8080/alpacalive/SelectLive.php";
    private ProgressDialog pd;
    RequestQueue queue;
    private String serverIP = "192.168.0.137";
    private String roomCode = "";
    private String roomUrl = "";
    private TextView eventName;
    ListView liveList;
    List<LiveList> dbLiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_listing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        liveList = findViewById(R.id.live_list);
        pd = new ProgressDialog(this);
        dbLiveList = new ArrayList<>();
        
        eventName = findViewById(R.id.txtEventName);
        String retrievedEventName = getIntent().getExtras().getString("EVENT_NAME","");
        eventName.setText(retrievedEventName);

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        loadLiveList(getApplicationContext(), GET_URL);
        
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

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String randomString(int len){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
    
    
    private void loadLiveList(Context context, String url) {

        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pd.isShowing())
            pd.setMessage("Sync with server...");
        pd.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            dbLiveList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                
                                JSONObject liveListingResponse = (JSONObject) response.get(i);
                                int userID = liveListingResponse.getInt("userID");
                                int eventID = liveListingResponse.getInt("eventID");
                                String roomCode = liveListingResponse.getString("roomCode");
                                String roomName = liveListingResponse.getString("roomName");
                                int viewerCount = liveListingResponse.getInt("viewerCount");

                                String strEventID = "" + eventID;

                                if(strEventID.equals(getIntent().getExtras().getString("EVENT_ID","")))
                                    dbLiveList.add(new LiveList(userID,eventID,roomCode,roomName,viewerCount));

                            }
                            loadLive();
                            if (pd.isShowing())
                                pd.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pd.isShowing())
                            pd.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void loadLive() {
        final LiveListAdapter adapter = new LiveListAdapter(this, R.layout.roomlisting_item, dbLiveList);
        liveList.setAdapter(adapter);
        if(dbLiveList != null){
            int size = dbLiveList.size();
            if(size > 0)
                Toast.makeText(getApplicationContext(), "No. of record : " + size + ".", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "No record found.", Toast.LENGTH_SHORT).show();
        }
    }
}
