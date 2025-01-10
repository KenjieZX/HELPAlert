package com.example.assignment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private EditText editEventName, editEventDate;
    private Button btnSaveEvent, btnBack;
    private String subjectId, eventId;
    private FirebaseFirestore db;
    private Calendar selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(), MainPage.class);
                startActivity(reg);
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();

        // Get subjectId and eventId from Intent
        subjectId = getIntent().getStringExtra("subjectId");
        eventId = getIntent().getStringExtra("eventId");

        if (subjectId == null || eventId == null) {
            Log.e("EditEventActivity", "Subject ID or Event ID is null");
            Toast.makeText(this, "Error: Subject or Event ID not passed", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize views
        editEventName = findViewById(R.id.editEventName);
        editEventDate = findViewById(R.id.editEventDate);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnBack = findViewById(R.id.btnBack);

        // Initialize calendar for DatePicker
        selectedDate = Calendar.getInstance();



        // Set up DatePickerDialog for event date selection
        editEventDate.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditEventActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate.set(Calendar.YEAR, selectedYear);
                        selectedDate.set(Calendar.MONTH, selectedMonth);
                        selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);

                        // Format the selected date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(selectedDate.getTime());

                        // Set the formatted date in the EditText
                        editEventDate.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        // Load event data
        loadEventData();

        // Save changes when Save button is clicked
        btnSaveEvent.setOnClickListener(v -> {

            String updatedName = editEventName.getText().toString().trim();
            String updatedDate = editEventDate.getText().toString().trim();

            if (updatedName.isEmpty() || updatedDate.isEmpty()) {
                Toast.makeText(EditEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the event in Firestore
            db.collection("subjects")
                    .document(subjectId)
                    .collection("events")
                    .document(eventId)
                    .update("name", updatedName, "date", updatedDate)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(EditEventActivity.this, EventListActivity.class);
                        intent.putExtra("subjectId", subjectId);
                        intent.putExtra("eventId", eventId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                    });


        });
    }

        private void loadEventData() {
            db.collection("subjects")
                    .document(subjectId)
                    .collection("events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String eventName = documentSnapshot.getString("name");
                            String eventDate = documentSnapshot.getString("date");

                            // Populate fields with existing data
                            editEventName.setText(eventName);
                            editEventDate.setText(eventDate);

                            // Parse date to pre-select in the DatePicker
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date date = dateFormat.parse(eventDate);
                                if (date != null) {
                                    selectedDate.setTime(date);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditEventActivity.this, "Failed to load event data", Toast.LENGTH_SHORT).show();
                    });
        }

}
