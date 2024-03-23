package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mess_1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private HashMap<Integer, Class<?>> fragmentMap;
    private HashMap<Integer, Class<?>> activityMap;
    TextView mess_name;
    RecyclerView recyclerView;
    List<CategoriesDataClass> dataList ;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String restaurant_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess1);
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        toolbar.setTitle("FoodEase");
        setSupportActionBar(toolbar);
        mess_name = findViewById(R.id.mess_name);
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        mess_name.setText(name);



        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Mess_1.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem nav_logout = menu.findItem(R.id.nav_logout);
        View headerView = navigationView.getHeaderView(0);
        TextView UserEmail = headerView.findViewById(R.id.useremail);


      nav_logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(@NonNull MenuItem item) {
              mAuth.signOut();
              return false;
          }
      });

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Mess_1.this, Login.class));
                    finish();
                }
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            UserEmail.setText(userEmail);
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.my_orders, User_orderHistory.class);
        fragmentMap.put(R.id.my_cart,User_cart.class);
        fragmentMap.put(R.id.feedback, Feedback.class);
        fragmentMap.put(R.id.about, About.class);

        activityMap = new HashMap<>();
        activityMap.put(R.id.my_cart,User_cart.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(Mess_1.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        Categories_myAdapter adapter = new Categories_myAdapter(Mess_1.this , dataList);
        recyclerView.setAdapter(adapter);

        retrieveVendorUserKeyByName();
        Log.d("RestID","id:"+restaurant_id);

        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        eventListener = databaseReference.orderByChild("restaurant_id").equalTo(restaurant_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
              //  Log.d("Check function","Inside onData Change");
                for(DataSnapshot categorySnapshot : snapshot.getChildren()){
                    String name = categorySnapshot.child("name").getValue(String.class);
                    String imageUrl = categorySnapshot.child("image_url").getValue(String.class);
                   // Log.d("Name",name);
                   // Log.d("image_url",imageUrl);
                    CategoriesDataClass dataClass = new CategoriesDataClass(name, imageUrl,restaurant_id);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        Log.d("DataList", "Size: " + dataList.size());
        for (CategoriesDataClass data : dataList) {
            Log.d("DataList", "Name: " + data.getName() + ", Image URL: " + data.getImage_url() + " restaurant_id: " + data.getRestaurant_id() );
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        Class<?> activityClass = activityMap.get(item.getItemId());
        if (activityClass != null) {
            intent = new Intent(Mess_1.this, activityClass);
            startActivity(intent);
        }
        Class<?> fragmentClass = fragmentMap.get(item.getItemId());
        if (fragmentClass != null) {
            try {
                Fragment fragment = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void retrieveVendorUserKeyByName(){
        DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference("vendors");
        vendorRef.orderByChild("restaurant_name").equalTo(mess_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        retrieveRestaurantKey(userId);
                    }
                } else {
                    Toast.makeText(Mess_1.this, "No vendor found for this key", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Mess_1.this, "Failed to fetch key for the vendor: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveRestaurantKey(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("vendors").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    restaurant_id = snapshot.child("restaurant_key").getValue(String.class);
                } else {
                    Toast.makeText(Mess_1.this, "No registered restaurant for id: " + userId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(Mess_1.this, "Failed to fetch restaurant key: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}