package com.example.loginpage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class Vendor_category_upload extends AppCompatActivity {

    ImageView uploadImage ;
    Button saveButton;
    EditText uploadName;
    String imageURL ;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_category_upload);

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
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
                            Toast.makeText(Vendor_category_upload.this, "No Image Selection", Toast.LENGTH_SHORT).show();
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
                fetchRestaurantDetails();
            }
        });
    }

    private void fetchRestaurantDetails() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("vendors").child(currentUserId);

        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot vendorSnapshot : snapshot.getChildren()) {
//                    String restaurantId = vendorSnapshot.child("key").getValue(String.class);
//                    if (restaurantId != null) {
//                        saveData(restaurantId);
//                        break;
//                    } else {
//                        Toast.makeText(Vendor_category_upload.this, "Restaurant ID not found for vendor", Toast.LENGTH_SHORT).show();
//                    }
//                }
                if (snapshot.exists()) {
                    String restaurantId = snapshot.child("key").getValue(String.class);
                    if (restaurantId != null) {
                        saveData(restaurantId);
                    } else {
                        Toast.makeText(Vendor_category_upload.this, "Restaurant ID not found for vendor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Vendor_category_upload.this, "Vendor details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Vendor_category_upload.this, "Failed to fetch restaurant details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveData(String restaurantId) {
        String categoryName = uploadName.getText().toString();
        String categoryId = FirebaseDatabase.getInstance().getReference("categories").push().getKey();

        StorageReference restaurantRef = FirebaseStorage.getInstance().getReference().child("vendors");
        StorageReference categoryRef = restaurantRef.child(categoryName);
        StorageReference imageRef = categoryRef.child(categoryId);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_category_upload.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        dialog.dismiss();
                        uploadData(restaurantId,categoryName,categoryId);
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
    public void uploadData(String restaurantId , String categoryName, String categoryId){
        CategoriesDataClass dataClass = new CategoriesDataClass(categoryName,imageURL,restaurantId);
        dataClass.setKey(categoryId);

        FirebaseDatabase.getInstance().getReference("categories").child(categoryId)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Vendor_category_upload.this,"Saved",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Vendor_category_upload.this,Vendor_interface.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Vendor_category_upload.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}