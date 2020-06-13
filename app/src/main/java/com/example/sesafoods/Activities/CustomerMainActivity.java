package com.example.sesafoods.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sesafoods.Utils.CircularTransform;
import com.example.sesafoods.Fragments.OrderFragment;
import com.example.sesafoods.R;
import com.example.sesafoods.Fragments.RestaurantListFragment;
import com.example.sesafoods.Fragments.TrayFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.google.android.libraries.places.api.Places;
import com.google.android.gms.location.places.Places;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

//    private View view;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String id = acct.getId();

//        CollapsingToolbarLayout layout = findViewById(R.id.collapsing_toolbar_layout);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        AppBarConfiguration appBarConfiguration =
//                new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupWithNavController(layout, toolbar, navController, appBarConfiguration);

        /*.......................Hooks.....................*/
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        /*.....................Tool Bar..................*/
        setSupportActionBar(toolbar);
//        /*................Navigation Drawer Menu..........*/

        //Hide or show items

//        Menu menu = navigationView.getMenu();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        String screen = intent.getStringExtra("screen");

        if (Objects.equals(screen, "tray")) {
            //      navigationView.setCheckedItem(R.id.nav_tray);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new TrayFragment()).commit();
        }
        else if (Objects.equals(screen, "order")) {
            //      navigationView.setCheckedItem(R.id.nav_order);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new OrderFragment()).commit();
        } else {
            //      navigationView.setCheckedItem(R.id.nav_restaurant);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new RestaurantListFragment()).commit();
        }



        // Get users info
//        if (acct != null) {
//            View header = navigationView.getHeaderView(0);
//            ImageView customer_avatar = (ImageView) header.findViewById(R.id.customer_avatar);
//            TextView customer_name = (TextView) header.findViewById(R.id.customer_name);
//
//            customer_name.setText(acct.getDisplayName());
//            Picasso.with(this).load(acct.getPhotoUrl()).transform(new CircularTransform()).into(customer_avatar);
//
////            String personName = acct.getDisplayName();
////            String personEmail = acct.getEmail();
//////            String personId = acct.getId();
////            Uri personPhoto = acct.getPhotoUrl();
//
//        } else {
        sharedPref = getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);

        View header = navigationView.getHeaderView(0);
        ImageView customer_avatar = (ImageView) header.findViewById(R.id.customer_avatar);
        TextView customer_name = (TextView) header.findViewById(R.id.customer_name);

        customer_name.setText(sharedPref.getString("name", ""));
        Picasso.with(this).load(sharedPref.getString("avatar", "")).transform(new CircularTransform()).into(customer_avatar);
//        }
    }



    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_restaurant:
                FragmentTransaction restaurant = getSupportFragmentManager().beginTransaction();
                restaurant.replace(R.id.content_frame, new RestaurantListFragment()).commit();
                break;
            case R.id.nav_tray:
                FragmentTransaction tray = getSupportFragmentManager().beginTransaction();
                tray.replace(R.id.content_frame, new TrayFragment()).commit();
                break;
            case R.id.nav_order:
                FragmentTransaction order = getSupportFragmentManager().beginTransaction();
                order.replace(R.id.content_frame, new OrderFragment()).commit();
                break;
            case R.id.nav_logout:
                logoutToServer(sharedPref.getString("token", ""));
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("token");
                editor.apply();

//                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//
//                String id = acct.getId();
//                if (id != null) {
//                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
//                        @Override
//                        public void onResult(@NonNull Status status) {
//                            if (status.isSuccess()) {
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            } else {}
//                        }
//                    });
//                } else {
//                    finishAffinity();
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                }

                finishAffinity();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);


                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logoutToServer(final String token) {
        String url = getString(R.string.API_URL) + "/social/revoke-token";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url,new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Execute code
                        Log.d("RESPONSE FROM SERVER", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("client_id", getString(R.string.CLIENT_ID));
                params.put("client_secret", getString(R.string.CLIENT_SECRET));

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

}
