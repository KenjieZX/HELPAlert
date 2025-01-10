package com.example.assignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private StorageReference storageRef;

    private RecyclerView recyclerViewSubjects;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;

    private ImageView selectedImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        String role = getIntent().getStringExtra("role");
        if (role.equals("admin")) {
        setContentView(R.layout.activity_admin);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        recyclerViewSubjects = findViewById(R.id.recycler_view_subjects);
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(this, subjectList);
        recyclerViewSubjects.setAdapter(subjectAdapter);

        subjectAdapter.setSubjectActionListener(new SubjectAdapter.SubjectActionListener() {
            @Override
            public void onEditClick(Subject subject) {
                showEditDialog(subject);
            }

            @Override
            public void onDeleteClick(Subject subject) {
                deleteSubject(subject);
            }
        });

        Button addSubjectButton = findViewById(R.id.addSubjectButton);
        addSubjectButton.setOnClickListener(view -> addSubject());

//         WITH IMAGES
        Button selectImageButton = findViewById(R.id.selectImage);
        selectedImageView = findViewById(R.id.selectedImage);
        selectImageButton.setOnClickListener(view -> selectImage());

        loadSubjects();
        } else if (role.equals("user")) {
            setContentView(R.layout.activity_main_page);
        }
    }

    private void loadSubjects() {
        CollectionReference subjectsRef = db.collection("subjects");
        subjectsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                subjectList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Subject subject = document.toObject(Subject.class);

                    Log.d("FirestoreData", "Loaded subject: " + subject.getName() + ", ID: " + subject.getId());

                    subject.setId(document.getId()); // Set document ID here

                    subjectList.add(subject);
                }
                subjectAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainPage.this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ADD SUBJECT WITH IMAGES
//    private void addSubject() {
//        EditText subjectNameEditText = findViewById(R.id.subjectName);
//        EditText subjectCodeEditText = findViewById(R.id.subjectCode);
//
//        String subjectName = subjectNameEditText.getText().toString().trim();
//        String subjectCode = subjectCodeEditText.getText().toString().trim();
//
//        if (subjectName.isEmpty() || subjectCode.isEmpty() || selectedImageUri == null) {
//            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String imageName = "images/" + System.currentTimeMillis() + ".jpg";
//        StorageReference imageRef = storageRef.child(imageName);
//        imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
//            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageUrl = uri.toString();
//
//                Subject newSubject = new Subject();
//                newSubject.setName(subjectName);
//                newSubject.setCode(subjectCode);
//                newSubject.setImageUrl(imageUrl);
//
//                db.collection("subjects")
//                        .add(newSubject)
//                        .addOnSuccessListener(documentReference -> {
//                            Toast.makeText(MainPage.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
//                            subjectNameEditText.setText("");
//                            subjectCodeEditText.setText("");
//                            selectedImageView.setImageURI(null);
//                            selectedImageUri = null;
//                            loadSubjects();
//                        })
//                        .addOnFailureListener(e -> Toast.makeText(MainPage.this, "Failed to add subject", Toast.LENGTH_SHORT).show());
//            });
//        });
//    }


    // ADD SUBJECT WITH NO IMAGES
    private void addSubject() {
        EditText subjectNameEditText = findViewById(R.id.subjectName);
        EditText subjectCodeEditText = findViewById(R.id.subjectCode);

        String subjectName = subjectNameEditText.getText().toString().trim();
        String subjectCode = subjectCodeEditText.getText().toString().trim();

//        if (subjectName.isEmpty() || subjectCode.isEmpty() || selectedImageUri == null) {
        if (subjectName.isEmpty() || subjectCode.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageName = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(imageName);
//        imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
//            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageUrl = uri.toString();

        Subject newSubject = new Subject();
        newSubject.setName(subjectName);
        newSubject.setCode(subjectCode);
//                newSubject.setImageUrl(imageUrl);

        db.collection("subjects")
                .add(newSubject)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainPage.this, "Subject added successfully", Toast.LENGTH_SHORT).show();
                    subjectNameEditText.setText("");
                    subjectCodeEditText.setText("");
//                            selectedImageView.setImageURI(null);
//                            selectedImageUri = null;
                    loadSubjects();
                })
                .addOnFailureListener(e -> Toast.makeText(MainPage.this, "Failed to add subject", Toast.LENGTH_SHORT).show());
//            });
//        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteSubject(Subject subject) {
        db.collection("subjects").document(subject.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainPage.this, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                    loadSubjects();
                })
                .addOnFailureListener(e -> Toast.makeText(MainPage.this, "Failed to delete subject", Toast.LENGTH_SHORT).show());
    }

    private void showEditDialog(Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.edit_subject, null);
        builder.setView(dialogView);

        EditText editSubjectName = dialogView.findViewById(R.id.editSubjectName);
        EditText editSubjectCode = dialogView.findViewById(R.id.editSubjectCode);

        editSubjectName.setText(subject.getName());
        editSubjectCode.setText(subject.getCode());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedName = editSubjectName.getText().toString().trim();
            String updatedCode = editSubjectCode.getText().toString().trim();

            if (updatedName.isEmpty() || updatedCode.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            subject.setName(updatedName);
            subject.setCode(updatedCode);

            db.collection("subjects").document(subject.getId())
                    .set(subject)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainPage.this, "Subject updated successfully", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainPage.this, "Failed to update subject", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }



}



















