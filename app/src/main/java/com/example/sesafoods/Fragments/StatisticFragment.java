package com.example.sesafoods.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sesafoods.R;
import com.example.sesafoods.Utils.CircularTransform;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {

    private BarChart chart;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chart = getActivity().findViewById(R.id.chart);

        // Get the driver's revenue
        getDriverRevenue();
    }

    private void dummyChart(JSONObject response) {

        JSONObject revenueJSONObject = null;

        try {
            revenueJSONObject = response.getJSONObject("revenue");

            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, revenueJSONObject.getInt("Mon")));
            entries.add(new BarEntry(1f, revenueJSONObject.getInt("Tue")));
            entries.add(new BarEntry(2f, revenueJSONObject.getInt("Wed")));
            entries.add(new BarEntry(3f, revenueJSONObject.getInt("Thu")));
            entries.add(new BarEntry(4f, revenueJSONObject.getInt("Fri")));
            entries.add(new BarEntry(5f, revenueJSONObject.getInt("Sat")));
            entries.add(new BarEntry(6f, revenueJSONObject.getInt("Sun")));

            BarDataSet set = new BarDataSet(entries, "Revenue by day");
            set.setColor(getResources().getColor(R.color.colorAccent));

            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width
            chart.setData(data);  // make the x-axis fit exactly all bars
            chart.setFitBars(true); // refresh

            // Labels that should be drawn on the X-axis
            final String[] days = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

            ValueFormatter formatter = new ValueFormatter() {

                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return days[(int) value];
                }
            };
            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1F);
            xAxis.setValueFormatter(formatter);

            chart.setDescription(null);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis axisRight = chart.getAxisRight();
            axisRight.setEnabled(false);

            YAxis axisLeft = chart.getAxisLeft();
            axisLeft.setAxisMinimum((float) 0.0);
            axisLeft.setAxisMaximum((float) 100.0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDriverRevenue() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MY_KEY", Context.MODE_PRIVATE);
        String url = getString(R.string.API_URL) + "/driver/revenue/?access_token=" + sharedPref.getString("token", "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("GET DRIVER REVENUE", response.toString());

                        dummyChart(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jsonObjectRequest);
    }
}
