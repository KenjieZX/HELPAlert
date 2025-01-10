package com.example.assignment;

public class Event {
    private String eventId;
    private String name;
    private String date;

    // Default constructor (required for Firestore)
    public Event() {
        // Empty constructor for Firestore deserialization
    }

    // Constructor with parameters (useful for adding/editing events)
    public Event(String eventId, String name, String date) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
    }

    // Getter and Setter for eventId
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    // Getter and Setter for event name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for event date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}