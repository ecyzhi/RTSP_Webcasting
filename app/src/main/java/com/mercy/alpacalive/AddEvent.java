package com.mercy.alpacalive;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    private Button btnAddEvent, btnStartDate, btnEndDate, btnCancel;
    private EditText eventName, eventLocation, eventDetails;
    private TextView startDate, endDate;

    private SharedPreferences sharedPref;
    private String sharedPrefFile = "com.mercy.alpacalive";
    private String GET_URL = "";

    private int startYear, startMonth, startDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        btnAddEvent = findViewById(R.id.btnConfirm);
        eventName = findViewById(R.id.txtEventName);
        eventLocation = findViewById(R.id.txtEventLocation);
        eventDetails = findViewById(R.id.txtDetails);

        this.showDatePickerDialog();

        sharedPref = getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
        final String serverIP = sharedPref.getString("SERVER_IP","");
        GET_URL = "http://" + serverIP + ":8080/alpacalive/InsertEvent.php";

        btnCancel = findViewById(R.id.cancelButton);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void showDatePickerDialog() {
        startDate = findViewById(R.id.txtStartDate);
        btnStartDate = findViewById(R.id.btnStart);

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(year);
                        strBuf.append("-");
                        if (month < 10) {
                            strBuf.append(0);
                        }
                        strBuf.append(month + 1);
                        strBuf.append("-");
                        if (dayOfMonth < 10) {
                            strBuf.append(0);
                        }
                        strBuf.append(dayOfMonth);
                        Calendar currentDate = Calendar.getInstance();

                        //check date
                        // year >> mm >> dd
                        if( year < currentDate.get(Calendar.YEAR)||
                                (year == currentDate.get(Calendar.YEAR) && month < currentDate.get(Calendar.MONTH))||
                                (year == currentDate.get(Calendar.YEAR) && month == currentDate.get(Calendar.MONTH) && dayOfMonth < currentDate.get((Calendar.DAY_OF_MONTH))) ){
                            startDate.setText(strBuf.toString());
                            startDate.requestFocus();
                            startDate.setError("Please do not choose past date");

                            btnAddEvent.setClickable(false);
                            btnAddEvent.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        }
                        else {
                            startYear = year;
                            startMonth = month;
                            startDay = dayOfMonth;
                            startDate.setError(null);
                            startDate.setText(strBuf.toString());
                            btnAddEvent.getBackground().setColorFilter(null);
                            btnAddEvent.setClickable(true);
                        }
                    }
                };

                //BUT the date picker will go back to current date if start date is different from current date
                // Get current year, month and day.
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);

                // Create the new DatePickerDialog instance.
                //DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, onDateSetListener, year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, android.R.style.Theme_Holo_Light_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Please select date.");
                // Popup the dialog.
                datePickerDialog.show();

            }
        });


        endDate = findViewById(R.id.txtEndDate);
        btnEndDate = findViewById(R.id.btnEnd);

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(year);
                        strBuf.append("-");
                        if (month < 10) {
                            strBuf.append(0);
                        }
                        strBuf.append(month + 1);
                        strBuf.append("-");
                        if (dayOfMonth < 10) {
                            strBuf.append(0);
                        }
                        strBuf.append(dayOfMonth);

                        //check end date
                        Calendar currentDate = Calendar.getInstance();

                        if( year < startYear||
                                (year == startYear && month < startMonth)||
                                (year == startYear && month == startMonth && dayOfMonth < startDay)){
                            endDate.setText(strBuf.toString());
                            endDate.requestFocus();
                            endDate.setError("End date must not before start date");

                            btnAddEvent.setClickable(false);
                            btnAddEvent.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        }

                        else if( year < currentDate.get(Calendar.YEAR)||
                                (year == currentDate.get(Calendar.YEAR) && month < currentDate.get(Calendar.MONTH))||
                                (year == currentDate.get(Calendar.YEAR) && month == currentDate.get(Calendar.MONTH) && dayOfMonth < currentDate.get((Calendar.DAY_OF_MONTH))) ){
                            endDate.setText(strBuf.toString());
                            endDate.requestFocus();
                            endDate.setError("Please do not choose past date");

                            btnAddEvent.setClickable(false);
                            btnAddEvent.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        }
                        else {

                            endDate.setError(null);
                            endDate.setText(strBuf.toString());
                            btnAddEvent.getBackground().setColorFilter(null);
                            btnAddEvent.setClickable(true);
                        }

                    }
                };

                // Get current year, month and day.
                Calendar now = Calendar.getInstance();
                int year = startYear;
                int month = startMonth;
                int day = startDay;

                // Create the new DatePickerDialog instance.
                //DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, onDateSetListener, year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, android.R.style.Theme_Holo_Light_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Please select date.");
                // Popup the dialog.
                datePickerDialog.show();

            }
        });
    }


    public void addEventtoDB(View view) {
//        progressDialog.setMessage("Please Wait, Event is being adding!");
//        progressDialog.show();

        final String name = eventName.getText().toString();
        final String location = eventLocation.getText().toString();
        final String start = startDate.getText().toString();
        final String end = endDate.getText().toString();
        final String details = eventDetails.getText().toString();

        valueGetFrom(name, location, start, end, details);

//        Intent intent = new Intent(this, EventListing.class);
//        startActivity(intent);
        //instead of call new activity, just kill the activity
        finish();
    }

    private void valueGetFrom(final String name, final String location, final String start, final String end, final String details) {
        StringRequest strReq = new StringRequest(Request.Method.POST, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                progressDialog.dismiss();
                Toast.makeText(AddEvent.this, "Event added successful.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
                Toast.makeText(AddEvent.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventName", name);
                params.put("eventLocation", location);
                params.put("eventStartDate", start);
                params.put("eventEndDate", end);
                params.put("eventDetails", details);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(AddEvent.this);
        requestQueue.add(strReq);

    }
}
