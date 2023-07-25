package com.example.collegeadmin.Faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.collegeadmin.R;
import com.example.collegeadmin.UploadImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProfessor extends AppCompatActivity {

    private ImageView addProfessorImage;
    private EditText addProfessorName;
    private EditText addProfessorEmail;
    private EditText addProfessorPost;
    private Spinner addProfessorCategory;
    private Button addProfessorBtn;
    private Bitmap bitmap;
    private String category;
    private String name, email, post, downloadUrl="";
    private ProgressDialog pd;
    private DatabaseReference reference, databaseReference;
    private StorageReference storageReference;
    private final int REQ = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_professor);

        addProfessorImage = findViewById(R.id.addProfessorImage);
        addProfessorName = findViewById(R.id.addProfessorName);
        addProfessorEmail = findViewById(R.id.addProfessorEmail);
        addProfessorPost = findViewById(R.id.addProfessorPost);
        addProfessorCategory = findViewById(R.id.addProfessorCategory);
        addProfessorBtn = findViewById(R.id.addProfessorBtn);
        pd = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Professor");
        storageReference = FirebaseStorage.getInstance().getReference();

        addProfessorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        String[] items = new String[]{"Select Category", "CSE", "IT", "CE", "ETC", "EEE"};
        addProfessorCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));

        addProfessorCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = addProfessorCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addProfessorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });



    }

    private void checkValidation() {
        name = addProfessorName.getText().toString();
        email = addProfessorEmail.getText().toString();
        post = addProfessorPost.getText().toString();

        if(name.isEmpty()){
            addProfessorName.setError("Empty");
            addProfessorName.requestFocus();
        }
        else if(email.isEmpty()){
            addProfessorEmail.setError("Empty");
            addProfessorEmail.requestFocus();
        }
        else if(post.isEmpty()){
            addProfessorPost.setError("Empty");
            addProfessorPost.requestFocus();
        }
        else if(category=="Select Category"){
            Toast.makeText(AddProfessor.this, "Please enter professor category", Toast.LENGTH_SHORT).show();
        }
        else if(bitmap==null){
            pd.setMessage("Uploading...");
            pd.show();
            insertData();
        }
        else{
            pd.setMessage("Uploading...");
            pd.show();
            insertImage();
        }
    }

    private void insertImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Professors").child(finalImage+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    insertData();
                                }
                            });
                        }
                    });
                }
                else{
                    pd.dismiss();
                    Toast.makeText(AddProfessor.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void insertData() {
        databaseReference = reference.child(category);
        final String uniqueKey = databaseReference.push().getKey();

        ProfessorData professorData = new ProfessorData(name, email, post, downloadUrl, uniqueKey);

        databaseReference.child(uniqueKey).setValue(professorData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddProfessor.this, "Professor added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProfessor.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addProfessorImage.setImageBitmap(bitmap);
        }
    }
}