package com.example.assignment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context context;
    private List<Subject> subjectList;
    private SubjectActionListener listener;



    public interface SubjectActionListener {
        void onEditClick(Subject subject);
        void onDeleteClick(Subject subject);
    }

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    public void setSubjectActionListener(SubjectActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);

        holder.subjectName.setText(subject.getName());
        holder.subjectCode.setText(subject.getCode());

        holder.edit.setOnClickListener(v -> listener.onEditClick(subject));
        holder.delete.setOnClickListener(v -> listener.onDeleteClick(subject));

//        if (subject.getImageUrl() != null) {
//            Picasso.get().load(subject.getImageUrl()).into(holder.subjectImage); // or Glide

//        } else {
//            holder.subjectImage.setImageResource(R.drawable.images); // Fallback image
//        }

        holder.edit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(subject);
            }
        });

        holder.delete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(subject);
            }
        });

        // Handle "Add Event" button click
//        holder.event.setOnClickListener(v -> {
//            Intent intent = new Intent(context, AddEventActivity.class);
//            intent.putExtra("subjectId", subject.getId());  // Pass subjectId to AddEventActivity
//            context.startActivity(intent);
//        });

        holder.event.setOnClickListener(v -> {
            String subjectId = subject.getId();
            if (subjectId != null) {
                Intent intent = new Intent(context, AddEventActivity.class);
                intent.putExtra("subjectId", subjectId);  // Pass subjectId to AddEventActivity
                context.startActivity(intent);
            } else {
                Log.e("SubjectAdapter", "Subject ID is null");
                Toast.makeText(context, "Error: Subject ID is missing", Toast.LENGTH_SHORT).show();
            }
        });

        holder.eventEdit.setOnClickListener(v -> {
            String subjectId = subject.getId();
            if (subjectId != null) {
                Intent intent = new Intent(context, EventListActivity.class);
                intent.putExtra("subjectId", subjectId);  // Pass subjectId to AddEventActivity
                context.startActivity(intent);
            } else {
                Log.e("SubjectAdapterEDITEVENT", "Subject ID is null");
                Toast.makeText(context, "Error: Subject ID is missing", Toast.LENGTH_SHORT).show();
            }
        });


        // Load image using Glide (you need to add Glide to your dependencies)
        Glide.with(context)
                .load(subject.getImageUrl())
                .placeholder(R.drawable.images)
                .into(holder.subjectImage);
    }




    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    // ViewHolder class
    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, subjectCode;
        ImageView subjectImage;
        Button edit, delete, event, eventEdit;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectCode = itemView.findViewById(R.id.subjectCodeText);
            subjectImage = itemView.findViewById(R.id.subjectImage);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);

            event = itemView.findViewById(R.id.btnEvent);
            eventEdit = itemView.findViewById(R.id.btnEventEdit);

        }
    }
}