package com.mercy.alpacalive;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEvent extends AppCompatActivity {

    private Button btnAddEvent;
    private ImageButton btnStartDate;
    private EditText eventName, eventLocation, eventDetails, startDate;

    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog dpd;

//    RequestQueue requestQueue;
//    ProgressDialog progressDialog;
//    String URL= "https://192.168.0.131/InsertEvent.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        btnAddEvent = findViewById(R.id.btnConfirm);
        eventName = findViewById(R.id.txtEventName);
        eventLocation = findViewById(R.id.txtEventLocation);
        eventDetails = findViewById(R.id.txtDetails);
        startDate = findViewById(R.id.txtStartDate);


        this.showDatePickerDialog();

    }

    private void showDatePickerDialog() {
        btnStartDate = findViewById(R.id.imgbtnStart);
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append(year);
                        strBuf.append("-");
                        if(month <10){
                            strBuf.append(0);
                        }
                        strBuf.append(month+1);
                        strBuf.append("-");
                        if(dayOfMonth<10){
                            strBuf.append(0);
                        }
                        strBuf.append(dayOfMonth);

                        startDate.setText(strBuf.toString());
                    }
                };
                // Get current year, month and day.
                Calendar now = Calendar.getInstance();
                int year = now.get(java.util.Calendar.YEAR);
                int month = now.get(java.util.Calendar.MONTH);
                int day = now.get(java.util.Calendar.DAY_OF_MONTH);



                // Create the new DatePickerDialog instance.
                //DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, onDateSetListener, year, month, day);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this, android.R.style.Theme_Holo_Light_Dialog, onDateSetListener, year, month, day);
                datePickerDialog.setTitle("Please select date.");
                // Popup the dialog.
                datePickerDialog.show();
            }
        });
    }


    public void addEventtoDB (View view){
//        Intent intent = new Intent(this, EventListing.class);
//        startActivity(intent);
        //instead of call new activity, just kill the activity
        finish();
    }
}
