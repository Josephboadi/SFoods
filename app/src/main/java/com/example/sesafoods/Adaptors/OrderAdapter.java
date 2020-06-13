package com.example.sesafoods.Adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sesafoods.Activities.CustomerMainActivity;
import com.example.sesafoods.Activities.DriverMainActivity;
import com.example.sesafoods.Activities.MealDetailActivity;
import com.example.sesafoods.Activities.MealListActivity;
import com.example.sesafoods.Fragments.DeliveryFragment;
import com.example.sesafoods.Objects.Order;
import com.example.sesafoods.Objects.Restaurant;
import com.example.sesafoods.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Order> orderList;

    public OrderAdapter(Activity activity, ArrayList<Order> orderList) {
        this.activity = activity;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_order, null);
        }

        final Order order = orderList.get(position);

        TextView restaurantName =(TextView) convertView.findViewById(R.id.restaurant_name);
        TextView customerName =(TextView) convertView.findViewById(R.id.customer_name);
        TextView customerAddress =(TextView) convertView.findViewById(R.id.customer_address);
        ImageView customerImage =(ImageView) convertView.findViewById(R.id.customer_image);

        restaurantName.setText(order.getRestaurantName());
        customerName.setText(order.getCustomerName());
        customerAddress.setText(order.getCustomerAddress());
        Picasso.with(activity.getApplicationContext()).load(order.getCustomerImage()).fit().into(customerImage);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show an alert
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Pick this order?");
                builder.setMessage("Would you like to take this order?");
                builder.setPositiveButton("Cancel", null);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity.getApplicationContext(), "ORDER PICKED", Toast.LENGTH_SHORT).show();

                        pickOrder(order.getId());
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }

    private void pickOrder(final String orderId) {
        String url = activity.getString(R.string.API_URL) + "/driver/order/pick/";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url,new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Execute code
                        Log.d("ORDER PICKED", response.toString());
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            if (jsonObj.getString("status").equals("success")) {
                                FragmentTransaction transaction = ((DriverMainActivity) activity).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content_frame, new DeliveryFragment()).commit();
                            } else {
                                Toast.makeText(activity, jsonObj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final SharedPreferences sharedPref = activity.getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", sharedPref.getString("token", ""));
                params.put("order_id", orderId);

                return params;
            }
        };
        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(postRequest);
    }
}
