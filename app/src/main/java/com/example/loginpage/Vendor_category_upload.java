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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
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

        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("vendors").child(currentUserId);

        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String restaurantId = snapshot.child("key").getValue(String.class);
                    String restaurantName = snapshot.child("restaurant_name").getValue(String.class);
                    if (restaurantId != null) {
                        saveData(restaurantId,restaurantName);
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


    private void saveData(String restaurantId,String restaurantName) {
        String categoryName = uploadName.getText().toString();
        String categoryId = FirebaseDatabase.getInstance().getReference("categories").push().getKey();

        StorageReference restaurantRef = FirebaseStorage.getInstance().getReference().child("vendors").child(restaurantName);
        StorageReference categoryRef = restaurantRef.child(categoryName);
        assert categoryId != null;
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(Vendor_category_upload.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        CategoriesDataClass dataClass = new CategoriesDataClass(categoryName,imageURL,restaurantId,categoryId);

        appendCategoryToRestaurant(restaurantId,categoryId);

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
    private void appendCategoryToRestaurant(String restaurantId, String categoryId) {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId).child("categories");
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        categories.add(childSnapshot.getValue(String.class));
                    }
                    categories.add(categoryId);
                    restaurantRef.setValue(categories);
                } else {
                    List<String> categories = new ArrayList<>();
                    categories.add(categoryId);
                    restaurantRef.setValue(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error retrieving categories: " + error.getMessage());
            }
        });
    }

}