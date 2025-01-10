package com.example.assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private EventActionListener listener;

    public interface EventActionListener {
        void onEditEventClick(Event event);
        void onDeleteEventClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    public void setEventActionListener(EventActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventDate.setText(event.getDate());

        holder.editEvent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditEventClick(event);
            }
        });

        holder.deleteEvent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDate;
        Button editEvent, deleteEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDate = itemView.findViewById(R.id.eventDate);
            editEvent = itemView.findViewById(R.id.editEvent);
            deleteEvent = itemView.findViewById(R.id.deleteEvent);
        }
    }
}
