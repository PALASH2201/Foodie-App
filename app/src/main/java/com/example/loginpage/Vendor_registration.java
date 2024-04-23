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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class Vendor_registration extends AppCompatActivity {

    ImageView uploadImage ;
    Button saveButton;
    EditText uploadName , uploadOwnerName , uploadLocation,uploadContact;
    String imageURL ;

    Uri uri;

    DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");

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
                            StyleableToast.makeText(Vendor_registration.this, "No Image Selection", Toast.LENGTH_SHORT,R.style.warningToast).show();
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

        String child_folder = "Restaurant Logo";

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("vendors").child(child_folder);
        StorageReference restRef = storageReference.child(uploadName.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_registration.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        restRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        String restaurant_name = uploadName.getText().toString();
        String owner_name = uploadOwnerName.getText().toString();
        String location = uploadLocation.getText().toString();
        String phone = uploadContact.getText().toString();

        String restaurant_id = FirebaseDatabase.getInstance().getReference("vendors").push().getKey();

        VendorDataClass dataClass = new VendorDataClass(restaurant_name,owner_name,location,phone,imageURL);
        dataClass.setKey(restaurant_id);

        Map<String, Object> restaurantDetails = new HashMap<>();
        restaurantDetails.put("restaurant_id", restaurant_id);
        restaurantDetails.put("restaurant_name", restaurant_name);
        assert restaurant_id != null;
        restaurantsRef.child(restaurant_id).setValue(restaurantDetails);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("vendors").child(currentUserId)

                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            StyleableToast.makeText(Vendor_registration.this,"Details saved",Toast.LENGTH_SHORT,R.style.successToast).show();
                            startActivity(new Intent(Vendor_registration.this, Vendor_interface.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        StyleableToast.makeText(Vendor_registration.this, e.getMessage(),Toast.LENGTH_SHORT,R.style.failureToast).show();
                    }
                });
    }
}