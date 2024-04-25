package com.example.loginpage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import io.github.muddz.styleabletoast.StyleableToast;

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
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_orange));

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        uri = data.getData();
                        uploadImage.setImageURI(uri);
                    } else {
                        StyleableToast.makeText(Vendor_category_upload.this, "No Image Selection", Toast.LENGTH_SHORT,R.style.warningToast).show();
                    }
                }
        );
        uploadImage.setOnClickListener(v -> {
            Intent photocopier = new Intent(Intent.ACTION_PICK);
            photocopier.setType("image/*");
            activityResultLauncher.launch(photocopier);
        });
        saveButton.setOnClickListener(v -> fetchRestaurantDetails());
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
                        StyleableToast.makeText(Vendor_category_upload.this, "Restaurant ID not found for vendor", Toast.LENGTH_SHORT,R.style.failureToast).show();
                    }
                } else {
                    StyleableToast.makeText(Vendor_category_upload.this, "Vendor details not found", Toast.LENGTH_SHORT,R.style.failureToast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(Vendor_category_upload.this, "Failed to fetch restaurant details: " + error.getMessage(), Toast.LENGTH_SHORT,R.style.failureToast).show();
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

        imageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnSuccessListener(uri -> {
                imageURL = uri.toString();
                dialog.dismiss();
                uploadData(restaurantId,categoryName,categoryId);
                uploadImage.setImageResource(R.drawable.uploading);
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                StyleableToast.makeText(Vendor_category_upload.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT,R.style.failureToast).show();
            });
        }).addOnFailureListener(e -> dialog.dismiss());
    }
    public void uploadData(String restaurantId , String categoryName, String categoryId){
        CategoriesDataClass dataClass = new CategoriesDataClass(categoryName,imageURL,restaurantId,categoryId);

        appendCategoryToRestaurant(restaurantId,categoryId);

        FirebaseDatabase.getInstance().getReference("categories").child(categoryId)
                .setValue(dataClass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        StyleableToast.makeText(Vendor_category_upload.this,"Details saved!",Toast.LENGTH_SHORT,R.style.successToast).show();
                        startActivity(new Intent(Vendor_category_upload.this,Vendor_interface.class));
                        finish();
                    }
                }).addOnFailureListener(e -> StyleableToast.makeText(Vendor_category_upload.this, e.getMessage(),Toast.LENGTH_SHORT,R.style.failureToast).show());
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