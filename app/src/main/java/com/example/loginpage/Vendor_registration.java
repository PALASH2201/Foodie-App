package com.example.loginpage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Vendor_registration extends AppCompatActivity {

    ImageView uploadImage ;
    Button saveButton;
    EditText uploadName , uploadOwnerName , uploadLocation,uploadContact;
    String imageURL ;

    Uri uri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_registration);

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        uploadOwnerName = findViewById(R.id.uploadOwnerName);
        uploadLocation = findViewById(R.id.uploadLocation);
        uploadContact = findViewById(R.id.uploadContact);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(Vendor_registration.this, "No Image Selection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photocopier = new Intent(Intent.ACTION_PICK);
                photocopier.setType("image/*");
                activityResultLauncher.launch(photocopier);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }
    private void saveData() {

        String restaurant_id = FirebaseDatabase.getInstance().getReference("vendors").push().getKey();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("vendors").child(restaurant_id);
        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_registration.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        dialog.dismiss();
                        uploadData(restaurant_id);
                        uploadImage.setImageResource(R.drawable.uploading);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData(String restaurant_id){
        String restaurant_name = uploadName.getText().toString();
        String owner_name = uploadOwnerName.getText().toString();
        String location = uploadLocation.getText().toString();
        String phone = uploadContact.getText().toString();

        VendorDataClass dataClass = new VendorDataClass(restaurant_name,owner_name,location,phone,imageURL);
        dataClass.setKey(restaurant_id);

        FirebaseDatabase.getInstance().getReference("vendors").child(restaurant_id)

                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Vendor_registration.this,"Saved",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Vendor_registration.this, Login.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Vendor_registration.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}