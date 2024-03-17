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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


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

    private DrawerLayout drawerLayout;
    private HashMap<Integer, Class<?>> fragmentMap;
    private HashMap<Integer, Class<?>> activityMap;
    TextView mess_name;

    RecyclerView recyclerView;
    List<CategoriesDataClass> dataList ;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;

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
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME_1);
        mess_name.setText(name);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Mess_1.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView UserEmail = headerView.findViewById(R.id.useremail);

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

        String restaurantId = "restaurant1_id";
        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        eventListener = databaseReference.orderByChild("restaurant_id").equalTo(restaurantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot categorySnapshot : snapshot.getChildren()){
                    String name = categorySnapshot.child("name").getValue(String.class);
                    String imageUrl = categorySnapshot.child("image_url").getValue(String.class);
                    CategoriesDataClass dataClass = new CategoriesDataClass(name, imageUrl);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        Class<?> activityClass = activityMap.get(item.getItemId());
        if (activityClass != null) {
            // Create an intent for the corresponding activity
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
}