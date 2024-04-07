package com.example.loginpage;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class Vendor_Add_Menu extends AppCompatActivity {

    ImageView uploadImage ;
    Button saveButton;
    EditText uploadDishName , uploadDishDesc , uploadDishPrice;
    String imageURL ;

    String extra_restaurant_name , extra_category_name , extra_restaurant_id , extra_category_id;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_add_menu);

        Intent intent = getIntent();
        extra_restaurant_name = intent.getStringExtra(Vendor_menu_detail.EXTRA_REST_NAME);
        extra_category_name = intent.getStringExtra(Vendor_menu_detail.EXTRA_CAT_NAME);
        extra_restaurant_id = intent.getStringExtra(Vendor_menu_detail.EXTRA_REST_ID);
        extra_category_id = intent.getStringExtra(Vendor_menu_detail.EXTRA_CAT_ID);


        uploadImage = findViewById(R.id.uploadImage);
        uploadDishName = findViewById(R.id.uploadDishName);
        uploadDishDesc = findViewById(R.id.uploadDishDesc);
        uploadDishPrice = findViewById(R.id.uploadDishPrice);
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
                        Toast.makeText(Vendor_Add_Menu.this, "No Image Selection", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        uploadImage.setOnClickListener(v -> {
            Intent photocopier = new Intent(Intent.ACTION_PICK);
            photocopier.setType("image/*");
            activityResultLauncher.launch(photocopier);
        });
        saveButton.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Food Items").child(extra_restaurant_name);
        StorageReference catRef = storageReference.child(extra_category_name).child(uploadDishName.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_Add_Menu.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        catRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURL = uri.toString();
                        dialog.dismiss();
                        uploadData();
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

    public void uploadData(){
        String dish_name = uploadDishName.getText().toString();
        String dish_desc = uploadDishDesc.getText().toString();
        String dish_price = uploadDishPrice.getText().toString();

        String dish_id = FirebaseDatabase.getInstance().getReference("dishes").push().getKey();

        DishDataClass dataClass = new DishDataClass(dish_name,dish_desc,dish_price,imageURL,extra_restaurant_id,extra_category_id,dish_id);

        appendCategoryToRestaurant(dish_id,extra_category_id);

        assert dish_id != null;
        FirebaseDatabase.getInstance().getReference("dishes").child(dish_id)

                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Vendor_Add_Menu.this,"Saved",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Vendor_Add_Menu.this, Vendor_menu_detail.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Vendor_Add_Menu.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void appendCategoryToRestaurant(String dishId , String categoryId) {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("categories").child(categoryId).child("dishes");
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        categories.add(childSnapshot.getValue(String.class));
                    }
                    categories.add(dishId);
                    restaurantRef.setValue(categories);
                } else {
                    List<String> categories = new ArrayList<>();
                    categories.add(dishId);
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