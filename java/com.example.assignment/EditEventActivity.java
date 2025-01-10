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

        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(), MainPage.class);
                startActivity(reg);
            }
        });

        db = FirebaseFirestore.getInstance();

        // Initialize views
//        editEventName = findViewById(R.id.editEventName);
//        editEventDate = findViewById(R.id.editEventDate);
//        btnSaveEvent = findViewById(R.id.btnSaveEvent);
//
//        // Get eventId from Intent
//        eventId = getIntent().getStringExtra("eventId");

        // Get subjectId and eventId from Intent
        subjectId = getIntent().getStringExtra("subjectId");
        eventId = getIntent().getStringExtra("eventId");

        if (subjectId == null || eventId == null) {
            Log.e("EditEventActivity", "Subject ID or Event ID is null");
            Toast.makeText(this, "Error: Subject or Event ID not passed", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize UI elements
        editEventName = findViewById(R.id.editEventName);
        editEventDate = findViewById(R.id.editEventDate);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
//        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
        btnBack = findViewById(R.id.btnBack);

        // Initialize calendar for DatePicker
        selectedDate = Calendar.getInstance();

        // Fetch event data from Firestore using eventId
//        FirebaseFirestore.getInstance()
//                .collection("subjects")
//                .document("subjectId") // Use the actual subjectId passed
//                .collection("events")
//                .document(eventId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Event event = documentSnapshot.toObject(Event.class);
//                        if (event != null) {
//                            // Populate fields with existing event data
//                            editEventName.setText(event.getName());
//                            editEventDate.setText(event.getDate());
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(EditEventActivity.this, "Error loading event", Toast.LENGTH_SHORT).show();
//                });

//        btnDatePicker.setOnClickListener(v -> {
//            // Get the current date
//            final Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            // Show DatePickerDialog
//            DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this,
//                    (view, selectedYear, selectedMonth, selectedDay) -> {
//                        // Update the calendar with the selected date
//                        calendar.set(Calendar.YEAR, selectedYear);
//                        calendar.set(Calendar.MONTH, selectedMonth);
//                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
//
//                        // Now show TimePickerDialog
//                        new TimePickerDialog(EditEventActivity.this,
//                                (timePicker, selectedHour, selectedMinute) -> {
//                                    // Update the calendar with the selected time
//                                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
//                                    calendar.set(Calendar.MINUTE, selectedMinute);
//
//                                    // Format the final datetime
//                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//                                    String formattedDate = dateFormat.format(calendar.getTime());
//
//                                    // Set the formatted date to the EditText
//                                    editEventDate.setText(formattedDate);
//                                },
//                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
//                        ).show();
//                    },
//                    year, month, day);
//
//            datePickerDialog.show();
//        });


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
//            String eventName = editEventName.getText().toString().trim();
//            String eventDate = editEventDate.getText().toString().trim();
//
//            if (eventName.isEmpty() || eventDate.isEmpty()) {
//                Toast.makeText(EditEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
//
//            // Update event in Firestore
//            Event updatedEvent = new Event(eventId, eventName, eventDate);
//
//
//            FirebaseFirestore.getInstance()
//                    .collection("subjects")
//                    .document("subjectId") // Use the actual subjectId
//                    .collection("events")
//                    .document(eventId)
//                    .set(updatedEvent)
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
//                        finish(); // Close the activity and go back to the event list
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(EditEventActivity.this, "Error updating event", Toast.LENGTH_SHORT).show();
//                    });
//        });

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
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditEventActivity.this, "Failed to update event", Toast.LENGTH_SHORT).show();
                    });
        });

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
}
