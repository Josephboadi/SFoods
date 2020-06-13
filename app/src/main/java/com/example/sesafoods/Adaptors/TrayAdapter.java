package com.example.sesafoods.Adaptors;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sesafoods.Objects.Tray;
import com.example.sesafoods.R;

import java.util.ArrayList;

public class TrayAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Tray> trayList;

    public TrayAdapter(Activity activity, ArrayList<Tray> trayList) {
        this.activity = activity;
        this.trayList = trayList;
    }

    @Override
    public int getCount() {
        return trayList.size();
    }

    @Override
    public Object getItem(int position) {
        return trayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_tray, null);
        }

        TextView mealName = (TextView) convertView.findViewById(R.id.tray_meal_name);
        TextView mealQuantity = (TextView) convertView.findViewById(R.id.tray_meal_quantity);
        TextView mealSubTotal = (TextView) convertView.findViewById(R.id.tray_meal_subtotal);

        Tray tray = trayList.get(position);
        mealName.setText(tray.getMealName());
        mealQuantity.setText(tray.getMealQuantity() + "");
        mealSubTotal.setText("$" + (tray.getMealPrice() * tray.getMealQuantity()));

        return convertView;
    }
}
