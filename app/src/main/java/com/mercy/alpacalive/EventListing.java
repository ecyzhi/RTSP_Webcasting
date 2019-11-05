package com.mercy.alpacalive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mercy.alpacalive.adapter.EventList;
import com.mercy.alpacalive.adapter.EventListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventListing extends AppCompatActivity {

    private SharedPreferences sharedPref;
    public static final String TAG = "com.mercy.alpacalive";
    private String sharedPrefFile = "com.mercy.alpacalive";
    private static String GET_URL;
    private ProgressDialog pd;
    RequestQueue queue;
    ListView listEvent;
    List<EventList> dbeventlist;
    EventListAdapter adapter;
    private Button btnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_listing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        String serverIP = sharedPref.getString("SERVER_IP","");
        GET_URL = "http://" + serverIP + ":8080/alpacalive/SelectEvent.php";

        listEvent = findViewById(R.id.list_event);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEventList(getApplicationContext(), GET_URL);
                adapter.notifyDataSetChanged();
            }
        });
        pd = new ProgressDialog(this);
        dbeventlist = new ArrayList<>();



        FloatingActionButton fab = findViewById(R.id.fab);

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        loadEventList(getApplicationContext(), GET_URL);


        listEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getItemAtPosition(position);
                EventList itemAtPosition = (EventList) object;
//                String clicked = itemAtPosition.getEventName();
//                Toast.makeText(getApplicationContext(),clicked, Toast.LENGTH_SHORT).show();

                //click go to room list
                Intent intent = new Intent(EventListing.this, LiveListing.class);
                intent.putExtra("EVENT_ID", itemAtPosition.getEventID());
                intent.putExtra("EVENT_NAME", itemAtPosition.getEventName());
                startActivity(intent);
            }
        });

    }

    public void addEvent(View view){
        Intent intent = new Intent(this, AddEvent.class);
        startActivity(intent);
    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadEventList(Context context, String url) {
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
                            dbeventlist.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject eventListingResponse = (JSONObject) response.get(i);
                                String eventID = eventListingResponse.getString("eventID");
                                String eventName = eventListingResponse.getString("eventName");
                                String location = eventListingResponse.getString("eventLocation");
                                String start = eventListingResponse.getString("eventStartDate");
                                String end = eventListingResponse.getString("eventEndDate");
                                String details = eventListingResponse.getString("eventDetails");
                                int roomCount = eventListingResponse.getInt("roomCount");

                                dbeventlist.add(new EventList(eventID,eventName,location,start,end,details,roomCount));

                            }
                            loadEvent();
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

    private void loadEvent() {
        adapter = new EventListAdapter(this, R.layout.eventlisting_item, dbeventlist);
        listEvent.setAdapter(adapter);
        if(dbeventlist != null){
            int size = dbeventlist.size();
            if(size > 0)
                Toast.makeText(getApplicationContext(), "No. of record : " + size + ".", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "No record found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
