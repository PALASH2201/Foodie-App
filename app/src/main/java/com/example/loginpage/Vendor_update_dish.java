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

public class Vendor_update_dish extends AppCompatActivity {
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_update_dish);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView updatedDishName = findViewById(R.id.updatedDishName);
        TextView updatedDishDesc = findViewById(R.id.updatedDishDesc);
        TextView updatedDishPrice = findViewById(R.id.updatedDishPrice);
        ImageView updatedImage = findViewById(R.id.updatedImage);
        Button upload = findViewById(R.id.updateButton);

        Intent intent = getIntent();
        if (intent != null) {
            String existingName = intent.getStringExtra("Dish Name");
            String existingPrice = intent.getStringExtra("Dish Price");
            String existingDescription = intent.getStringExtra("Dish Description");
            String existingImageUrl = intent.getStringExtra("Dish Image");
            String existingCategoryName = intent.getStringExtra("Category Name");
            String existingRestaurantName = intent.getStringExtra("Restaurant Name");
            String existingDishId = intent.getStringExtra("Dish Id");

            updatedDishName.setText(existingName);
            updatedDishPrice.setText(existingPrice);
            updatedDishDesc.setText(existingDescription);

            Glide.with(Vendor_update_dish.this).load(existingImageUrl).into(updatedImage);

            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                Intent data = result.getData();
                                uri =  data.getData();
                                updatedImage.setImageURI(uri);
                            }else{
                                Toast.makeText(Vendor_update_dish.this,"No Image Selected" , Toast.LENGTH_SHORT).show();
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
                    String updatedName = updatedDishName.getText().toString().trim();
                    String updatedPrice = updatedDishPrice.getText().toString().trim();
                    String updatedDescription = updatedDishDesc.getText().toString().trim();
                    if (uri != null) {
                        updateDishDetails(updatedName, updatedPrice, updatedDescription,existingCategoryName,existingDishId,existingRestaurantName);
                    } else {
                        updateDishWithoutImage(updatedName, updatedPrice, updatedDescription,existingDishId);
                    }
                }
            });
        }

    }
    private void updateDishDetails(String updatedName, String updatedPrice, String updatedDescription,String categoryName,String dishId,String restaurantName) {
        uploadImageAndUpdateDish(updatedName, updatedPrice, updatedDescription,categoryName,dishId,restaurantName);
    }
    private void uploadImageAndUpdateDish(String updatedName, String updatedPrice, String updatedDescription,String categoryName,String dishId,String restaurantName) {
        if (uri != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("Food Items")
                    .child(restaurantName)
                    .child(categoryName)
                    .child(updatedName);
            imageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
                                    dishesRef.child("dish_image_url").setValue(uri.toString());
                                    dishesRef.child("dish_name").setValue(updatedName);
                                    dishesRef.child("dish_price").setValue(updatedPrice);
                                    dishesRef.child("dish_description").setValue(updatedDescription);
                                    Toast.makeText(Vendor_update_dish.this, "Dish details updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle image upload failure
                            Toast.makeText(Vendor_update_dish.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void updateDishWithoutImage(String updatedName, String updatedPrice, String updatedDescription,String dishId) {
        DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
        dishesRef.child("dish_name").setValue(updatedName);
        dishesRef.child("dish_price").setValue(updatedPrice);
        dishesRef.child("dish_description").setValue(updatedDescription);
        Toast.makeText(Vendor_update_dish.this, "Dish details updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}