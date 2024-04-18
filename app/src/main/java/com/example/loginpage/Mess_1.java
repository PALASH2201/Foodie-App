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

    private Categories_myAdapter adapter;
    private AlertDialog dialog;

    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private HashMap<Integer, Class<?>> fragmentMap;
    private HashMap<Integer, Class<?>> activityMap;
    TextView mess_name;
    RecyclerView recyclerView;
    String user_id,restaurant_name,restaurant_id;
    List<CategoriesDataClass> dataList ;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    public static final String EXTRA_CAT_NAME = "com.example.User_menu_detail.extra.CAT_NAME";
    public static final String EXTRA_REST_NAME = "com.example.User_menu_detail.extra.REST_NAME";
    public static final String EXTRA_CAT_ID = "com.example.User_menu_detail.extra.CAT_ID";
    public static final String EXTRA_REST_ID = "com.example.User_menu_detail.extra.REST_ID";

    @SuppressLint({"MissingInflatedId", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess1);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        user_id = mAuth.getCurrentUser().getUid();
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
        fragmentMap.put(R.id.feedback, Feedback.class);
        fragmentMap.put(R.id.about, About.class);

        activityMap = new HashMap<>();
        activityMap.put(R.id.my_cart,User_cart.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(Mess_1.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new Categories_myAdapter(Mess_1.this , dataList,false);
        recyclerView.setAdapter(adapter);

        retrieveRestaurantIdByName();

        adapter.setOnItemClickListener(new Categories_myAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isVendor) {
                if(!isVendor){
                    restaurant_name = mess_name.getText().toString();
                    restaurant_id = dataList.get(position).getRestaurant_id();

                    Intent intent = new Intent(Mess_1.this,User_menu_detail.class);
                    intent.putExtra(EXTRA_CAT_NAME,dataList.get(position).getName());
                    intent.putExtra(EXTRA_REST_NAME,restaurant_name);
                    intent.putExtra(EXTRA_CAT_ID,dataList.get(position).getKey());
                    intent.putExtra(EXTRA_REST_ID,restaurant_id);
                    startActivity(intent);
                }
            }

            @Override
            public void onEditClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        Class<?> fragmentClass = fragmentMap.get(item.getItemId());
        if (fragmentClass != null) {
            try {
                Fragment fragment = (Fragment) fragmentClass.newInstance();
                mess_name.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            } catch (Exception e) {
                Log.d("Navigation" , "No fragment");
            }
            return true;
        }
        if (item.getItemId() == R.id.my_cart) {
            checkCartAndNavigate(item);
        } else {
            Class<?> activityClass = activityMap.get(item.getItemId());
            if (activityClass != null) {
                intent = new Intent(Mess_1.this, activityClass);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void checkCartAndNavigate(MenuItem item) {
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("cart");
        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Intent intent = new Intent(Mess_1.this, User_cart.class);
                    intent.putExtra("User Id",user_id);
                    intent.putExtra("restaurant_name",restaurant_name);
                    intent.putExtra("restaurant_id",restaurant_id);
                    startActivity(intent);
                } else {
                    Toast.makeText(Mess_1.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Mess_1.this, "Failed to check cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveRestaurantIdByName() {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantRef.orderByChild("restaurant_name").equalTo(mess_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                        String restaurantId = restaurantSnapshot.getKey();
                        Log.d("Restaurant Id", "Id: " + restaurantId);
                        HandleDatabase(restaurantId);
                    }
                } else {
                    Toast.makeText(Mess_1.this, "No restaurant found with the specified name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Mess_1.this, "Failed to fetch restaurant ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void HandleDatabase(String restaurant_id){
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        eventListener = databaseReference.orderByChild("restaurant_id").equalTo(restaurant_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot categorySnapshot : snapshot.getChildren()){
                    String name = categorySnapshot.child("name").getValue(String.class);
                    String imageUrl = categorySnapshot.child("image_url").getValue(String.class);
                    String categoryId = categorySnapshot.child("key").getValue(String.class);
                    CategoriesDataClass dataClass = new CategoriesDataClass(name, imageUrl,restaurant_id,categoryId);
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

    }
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }

}