package com.example.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Vendor_update_category extends AppCompatActivity {

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_update_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView updatedName = findViewById(R.id.updatedName);
        ImageView updatedImage = findViewById(R.id.updatedImage);
        Button upload = findViewById(R.id.updateButton);

        Intent intent = getIntent();
        if (intent != null) {
            String existingName = intent.getStringExtra("Category Name");
            String existingImageUrl = intent.getStringExtra("Category Image");
            String existingRestaurantName = intent.getStringExtra("Restaurant Name");
            String existingCategoryId = intent.getStringExtra("Category Id");

            updatedName.setText(existingName);

            Glide.with(Vendor_update_category.this).load(existingImageUrl).into(updatedImage);

            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                Intent data = result.getData();
                                uri =  data.getData();
                                updatedImage.setImageURI(uri);
                            }else{
                                Toast.makeText(Vendor_update_category.this,"No Image Selected" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            updatedImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPicker = new Intent(Intent.ACTION_PICK);
                    photoPicker.setType("image/*");
                    activityResultLauncher.launch(photoPicker);
                }
            });

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updatedCategoryName = updatedName.getText().toString().trim();
                    if (uri != null) {
                        updateDishDetails(updatedCategoryName,existingCategoryId,existingRestaurantName);
                    } else {
                        updateDishWithoutImage(updatedCategoryName,existingCategoryId);
                    }
                }
            });
        }
    }

    private void updateDishDetails(String updatedCategoryName,String existingCategoryId,String restaurantName) {
        uploadImageAndUpdateDish(updatedCategoryName,existingCategoryId,restaurantName);
    }
    private void uploadImageAndUpdateDish(String updatedCategoryName,String existingCategoryId,String restaurantName) {
        if (uri != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("vendors")
                    .child(restaurantName)
                    .child(updatedCategoryName)
                    .child(existingCategoryId);
            imageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference catRef = FirebaseDatabase.getInstance().getReference("categories").child(existingCategoryId);
                                    catRef.child("image_url").setValue(uri.toString());
                                    catRef.child("name").setValue(updatedCategoryName);
                                    Toast.makeText(Vendor_update_category.this, "Category details updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle image upload failure
                            Toast.makeText(Vendor_update_category.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void updateDishWithoutImage(String updatedCategoryName,String categoryId) {
        DatabaseReference catRef = FirebaseDatabase.getInstance().getReference("dishes").child(categoryId);
        catRef.child("dish_name").setValue(updatedCategoryName);
        Toast.makeText(Vendor_update_category.this, "Category details updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

}