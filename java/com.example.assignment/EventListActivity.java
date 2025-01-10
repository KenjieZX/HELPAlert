package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView eventRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    private String subjectId; // Store subjectId to get the events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(getApplicationContext(), MainPage.class);
                startActivity(reg);
            }
        });



        Log.d("EventListActivity", "onCreate: Activity started");

        // Get subjectId from Intent
        subjectId = getIntent().getStringExtra("subjectId");
        Log.d("EventListActivity", "Subject ID: " + subjectId);

        if (subjectId == null) {
            Log.e("EventListActivity", "Error: Subject ID is null");
            Toast.makeText(this, "Subject ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }




        eventRecyclerView = findViewById(R.id.eventRecyclerView);

        db = FirebaseFirestore.getInstance();

        // Get the subject ID from the intent
//        subjectId = getIntent().getStringExtra("subjectId");

        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        eventRecyclerView.setAdapter(eventAdapter);

        Log.d("EventListActivity", "RecyclerView and adapter initialized");

        // Fetch the events for this subject
        fetchEventsForSubject();

        eventAdapter.setEventActionListener(new EventAdapter.EventActionListener() {
            @Override
            public void onEditEventClick(Event event) {
                // Navigate to EditEventActivity
                Intent intent = new Intent(EventListActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", event.getEventId());
                startActivity(intent);
            }

            @Override
            public void onDeleteEventClick(Event event) {
                // Delete the event
                deleteEvent(event);
            }
        });
    }

    private void fetchEventsForSubject() {
        db.collection("subjects")
                .document(subjectId)
                .collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        eventList.clear();

                        for (DocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);

                            event.setEventId(document.getId());

                            eventList.add(event);
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("EventListActivity", "Error getting events: ", task.getException());
                    }
                });
    }

    private void deleteEvent(Event event) {
        db.collection("subjects").document(subjectId)
                .collection("events")
                .document(event.getEventId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EventListActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                        eventList.remove(event);
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EventListActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
