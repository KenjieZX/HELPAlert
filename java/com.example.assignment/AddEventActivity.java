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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String subjectId;
    private EditText editEventName, editEventDate;
    private Button btnAddEvent, btnBack;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(), MainPage.class);
                startActivity(reg);
            }
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the subjectId from the Intent
        subjectId = getIntent().getStringExtra("subjectId");
        if (subjectId == null) {
            Log.e("AddEventActivity", "Subject ID is null");
            Toast.makeText(this, "Error: Subject ID not passed", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize UI elements
        editEventName = findViewById(R.id.editEventName);
        editEventDate = findViewById(R.id.editEventDate);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        // Initialize calendar for DatePicker
        selectedDate = Calendar.getInstance();

        // Set up DatePickerDialog for event date selection
        editEventDate.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddEventActivity.this,
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




        // Handle Add Event button click
        btnAddEvent.setOnClickListener(v -> {
            String eventName = editEventName.getText().toString().trim();
            String eventDate = editEventDate.getText().toString().trim();

            if (eventName.isEmpty() || eventDate.isEmpty()) {
                Toast.makeText(AddEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate unique event ID or use a timestamp
            String eventId = FirebaseFirestore.getInstance().collection("events").document().getId();

            // Create Event object
            Event event = new Event(eventId, eventName, eventDate);  // Add an empty description if needed.

            // Get the subject ID (you may need to pass this when navigating to AddEventActivity)
            String subjectId = getIntent().getStringExtra("subjectId"); // Assuming subjectId is passed when navigating

            // Save event under the correct subject in Firestore
            db.collection("subjects")
                    .document(subjectId)
                    .collection("events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after adding the event
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddEventActivity.this, "Failed to add event", Toast.LENGTH_SHORT).show();
                    });
        });
    }

}